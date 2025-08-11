package poliplanner.excel.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poliplanner.excel.parser.JsonLayoutLoader.Layout;
import poliplanner.exception.ExcelParserException;

@Component
@RequiredArgsConstructor
public class ExcelParser {
    private List<Layout> layouts;

    private static final Map<String, BiConsumer<SubjectCsvDTO, String>> fieldMapper = createFieldMappers();
    private static final Set<String> HEADER_KEYWORDS = Set.of("ítem", "item");

    ExcelParser(JsonLayoutLoader loader) {
        try {
            this.layouts = loader.loadJsonLayouts();
        } catch (Exception e) {
            // TODO: handle exception
            // FIX: ver que hacer con esto la verdad
        }
    }

    ExcelParser(List<Layout> layouts) {
        this.layouts = layouts;
    }

    // ================================
    // ======== Public API ============
    // ================================

    // TEST: exponer un metodo para hacer testing sobre sheets sueltas en vez de
    // sobre el el archivo completo, asi para poder testear la correcta
    // persistencia.
    public Map<String, List<SubjectCsvDTO>> parseExcel(Path file) throws ExcelParserException {
        try (InputStream is = new FileInputStream(new File(file.toUri()));
                ReadableWorkbook wb = new ReadableWorkbook(is)) {

            Map<String, List<SubjectCsvDTO>> result = new HashMap<>();
            for (Sheet sheet : wb.getSheets().toList()) {
                // Ignorar hojas "basura"
                String career = sheet.getName();
                if (career.contains("odigos")
                        || career.contains("ódigos")
                        || career.contains("Asignaturas")
                        || career.contains("Homologadas")
                        || career.contains("Homólogas")
                        || career.equalsIgnoreCase("códigos")) {
                    continue;
                }

                List<Row> sheetRows = sheet.read();

                Row headerRow = searchHeadersRow(sheetRows);
                // Este metodo es 1 based, asi que ahora esta apuntando a la fila inmediatamente
                // debajo de los encabezados.
                Integer startingRow = headerRow.getRowNum();
                Integer startingCell = calculateStartingCell(headerRow);

                System.out.println(career);
                Layout layout = findFittingLayout(headerRow);

                List<SubjectCsvDTO> subjects = new ArrayList<>();
                for (Integer i = startingRow; i < sheetRows.size(); i++) {
                    Row row = sheetRows.get(i);
                    SubjectCsvDTO subject = parseRow(row, layout, startingCell);

                    // Legamos al final de las materias
                    if (null == subject) {
                        break;
                    }

                    subjects.add(subject);
                }

                result.put(career, subjects);
            }

            return result;
        } catch (Exception e) {
            // FIX: dar un aviso de parsing error
            throw new ExcelParserException("Error de parseo: ", e);
        }
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    /**
     * Parsea una fila de Excel y la convierte en un objeto SubjectCsvDTO según el
     * layout especificado.
     * 
     * <p>
     * La función recorre cada celda de la fila a partir de la posición startingCell
     * y asigna los valores a las propiedades correspondientes del DTO basándose en
     * los encabezados definidos en el layout.
     * La función maneja todos los campos definidos en SubjectCsvDTO:
     * </p>
     * 
     * @param rowData      La fila de Excel a parsear (objeto Row de la librería
     *                     FastExcel)
     * @param layout       Objeto Layout que define la estructura y mapeo de los
     *                     encabezados
     * @param startingCell Número de celda (basado en 1) donde comienzan los datos a
     *                     parsear
     * @return Objeto SubjectCsvDTO poblado con los datos de la fila, o null si:
     *         - La fila está vacía (según isEmptyRow)
     *         - El layout tiene más encabezados que celdas disponibles en la fila
     * 
     * @throws ClassCastException Si el contenido de alguna celda no puede
     *                            convertirse a String
     * @see Layout
     * @see SubjectCsvDTO
     * @see #isEmptyRow(Row)
     */
    public static SubjectCsvDTO parseRow(Row rowData, Layout layout, Integer startingCell) {
        if (layout.headers.size() > rowData.getCellCount() || isEmptyRow(rowData)) {
            return null;
        }

        SubjectCsvDTO dto = new SubjectCsvDTO();
        Integer currentCell = startingCell - 1;
        for (String layoutField : layout.headers) {
            currentCell++;
            Cell rowCell = rowData.getCell(currentCell);
            if (rowCell == null) {
                continue;
            }

            String cellValue = (String) rowCell.getText();
            // Mapear a su respectivo elemento del DTO
            fieldMapper.getOrDefault(layoutField, (d, v) -> {
            }).accept(dto, cellValue);
        }

        return dto;
    }

    private static Row searchHeadersRow(List<Row> rows) throws FileNotFoundException, IOException {
        for (Row r : rows) {
            if (r.getCellCount() < 0 || !isHeader(r)) {
                continue;
            }
            return r;
        }

        return null;
    }

    /**
     * Busca y devuelve el primer {@link Layout} cuyo conjunto de encabezados
     * coincida con los valores de las celdas en la fila dada. La comparación de
     * encabezados se hace ignorando mayúsculas y minúsculas, y permitiendo celdas
     * vacías (se ignoran).
     * 
     * @param r Fila de entrada cuyas celdas se usan para verificar coincidencias.
     * @return El {@link Layout} que coincide con la fila.
     * @throws IllegalArgumentException Si no se encuentra ningún Layout que
     *                                  coincida.
     */
    public Layout findFittingLayout(Row r) {
        List<Cell> cells = r.stream().collect(Collectors.toList());

        for (Layout currentLayout : this.layouts) {
            boolean matches = true;
            int currentCell = -1;
            for (int i = 0; i < currentLayout.headers.size(); i++) {
                currentCell++;
                String actual = getStringCellValueSafe(cells, currentCell).trim();
                System.out.println("Actual:" + actual + " - Expected:" + currentLayout.headers.get(i));
                // Para simplemente saltear casillas que estan vacias (por si mueven la tabla
                // de lugar).
                if (actual.isEmpty()) {
                    i--;
                    continue;
                }

                List<String> expectedPatterns = currentLayout.patterns.get(currentLayout.headers.get(i));
                matches = false;
                for (String pattern : expectedPatterns) {
                    if (actual.contains(pattern)) {
                        matches = true;
                        continue;
                    }
                }

                // El patron es invalido, pasamos a otro
                if (!matches) {
                    break;
                }
            }

            // Earyly return el layout valido
            if (matches) {
                return currentLayout;
            }
        }

        throw new IllegalArgumentException("No matching layout found for row: " + r);
    }

    // =====================================
    // ======== Utility methods ============
    // =====================================

    private String getStringCellValueSafe(List<Cell> cells, int index) {
        if (index >= cells.size() || cells.get(index) == null) {
            return "";
        }
        return cells.get(index).getText().toLowerCase();
    }

    // Determina la fila del Excel de encabezados buscando la celda que
    // contenga el texto "ítem" o "item" (case insensitive).
    private static boolean isHeader(Row r) {
        // Implementación con Stream que:
        // 1. Filtra solo celdas de tipo STRING (not null)
        // 2. Normaliza el contenido (minúsculas y sin espacios)
        // 3. Early return al encontrar una coincidencia
        return r.stream()
                .filter(Objects::nonNull)
                .filter(cell -> cell.getType() == CellType.STRING)
                .map(cell -> ((String) cell.getValue()).toLowerCase().trim())
                .anyMatch(HEADER_KEYWORDS::contains);
    }

    private static boolean isEmptyRow(Row r) {
        return r.stream()
                .noneMatch(cell -> cell != null
                        && cell.getValue() != null
                        && !cell.getValue().toString().trim().isEmpty());
    }

    private Integer calculateStartingCell(Row r) {
        Integer index = 0;
        for (Cell cell : r) {
            if (cell == null || cell.getText().trim().isEmpty()) {
                index++;
                continue;
            }
            return index;
        }

        // NOTE: nunca se deberia de llegar aca
        return index;
    }

    private static Map<String, BiConsumer<SubjectCsvDTO, String>> createFieldMappers() {
        Map<String, BiConsumer<SubjectCsvDTO, String>> mappers = new HashMap<>();

        // Info general
        mappers.put("departamento", (dto, val) -> dto.departamento = val);
        mappers.put("asignatura", (dto, val) -> dto.nombreAsignatura = val);
        mappers.put("nivel", (dto, val) -> dto.nivel = val);
        mappers.put("semestre", (dto, val) -> dto.semestre = val);
        mappers.put("seccion", (dto, val) -> dto.seccion = val);

        // Info del docente
        mappers.put("titulo", (dto, val) -> dto.tituloProfesor = val);
        mappers.put("apellido", (dto, val) -> dto.apellidoProfesor = val);
        mappers.put("nombre", (dto, val) -> dto.nombreProfesor = val);
        mappers.put("correo", (dto, val) -> dto.emailProfesor = val);

        // Primer parcial
        mappers.put("diaParcial1", (dto, val) -> dto.parcial1Fecha = val);
        mappers.put("horaParcial1", (dto, val) -> dto.parcial1Hora = val);
        mappers.put("aulaParcial1", (dto, val) -> dto.parcial1Aula = val);

        // Segundo parcial
        mappers.put("diaParcial2", (dto, val) -> dto.parcial2Fecha = val);
        mappers.put("horaParcial2", (dto, val) -> dto.parcial2Hora = val);
        mappers.put("aulaParcial2", (dto, val) -> dto.parcial2Aula = val);

        // Primer Final
        mappers.put("diaFinal1", (dto, val) -> dto.final1Fecha = val);
        mappers.put("horaFinal1", (dto, val) -> dto.final1Hora = val);
        mappers.put("aulaFinal1", (dto, val) -> dto.final1Aula = val);

        // Segundo Final
        mappers.put("diaFinal2", (dto, val) -> dto.final2Fecha = val);
        mappers.put("horaFinal2", (dto, val) -> dto.final2Hora = val);
        mappers.put("aulaFinal2", (dto, val) -> dto.final2Aula = val);

        // Revisiones
        mappers.put("revisionDia", (dto, val) -> {
            dto.final1RevFecha = val;
            dto.final2RevFecha = val;
        });
        mappers.put("revisionHora", (dto, val) -> {
            dto.final1RevHora = val;
            dto.final2RevHora = val;
        });

        // Comité
        mappers.put("mesaPresidente", (dto, val) -> dto.comitePresidente = val);
        mappers.put("mesaMiembro1", (dto, val) -> dto.comiteMiembro1 = val);
        mappers.put("mesaMiembro2", (dto, val) -> dto.comiteMiembro2 = val);

        // Horario semanal
        mappers.put("aulaLunes", (dto, val) -> dto.aulaLunes = val);
        mappers.put("horaLunes", (dto, val) -> dto.lunes = val);
        mappers.put("aulaMartes", (dto, val) -> dto.aulaMartes = val);
        mappers.put("horaMartes", (dto, val) -> dto.martes = val);
        mappers.put("aulaMiercoles", (dto, val) -> dto.aulaMiercoles = val);
        mappers.put("horaMiercoles", (dto, val) -> dto.miercoles = val);
        mappers.put("aulaJueves", (dto, val) -> dto.aulaJueves = val);
        mappers.put("horaJueves", (dto, val) -> dto.jueves = val);
        mappers.put("aulaViernes", (dto, val) -> dto.aulaViernes = val);
        mappers.put("horaViernes", (dto, val) -> dto.viernes = val);
        mappers.put("aulaSabado", (dto, val) -> dto.aulaSabado = val);
        mappers.put("horaSabado", (dto, val) -> dto.sabado = val);
        mappers.put("fechasSabado", (dto, val) -> dto.fechasSabadoNoche = val);

        return mappers;
    }
}
