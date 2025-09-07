package poliplanner.excel.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;

import poliplanner.excel.exception.ExcelParserConfigurationException;
import poliplanner.excel.parser.JsonLayoutLoader.Layout;
import poliplanner.excel.exception.ExcelParserException;
import poliplanner.excel.exception.ExcelParserInputException;
import poliplanner.excel.exception.LayoutMatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ExcelParser {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelParser.class);
    private List<Layout> layouts;

    private static final Map<String, BiConsumer<SubjectCsvDTO, String>> fieldMapper = createFieldMappers();
    private static final Set<String> HEADER_KEYWORDS = Set.of("ítem", "item");

    public ExcelParser() {
        try {
            JsonLayoutLoader loader = new JsonLayoutLoader();
            this.layouts = loader.loadJsonLayouts();
        } catch (IOException e) {
            throw new ExcelParserConfigurationException("Failed to load layouts: ", e);
        }
    }

    // ================================
    // ======== Public API ============
    // ================================

    public Map<String, List<SubjectCsvDTO>> parseExcel(File file) throws ExcelParserException {
        try (InputStream is = new FileInputStream(file);
                ReadableWorkbook wb = new ReadableWorkbook(is)) {

            LOG.info("Parsing file: {}", file.toString());

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
                    LOG.info("Ignoring sheet: {}", career);
                    continue;
                }
                LOG.info("Parsing sheet: {}", career);

                List<SubjectCsvDTO> subjects = parseSheet(sheet);
                result.put(career, subjects);
            }

            return result;
        } catch (FileNotFoundException e) {
            throw new ExcelParserConfigurationException("File not found: " + file, e);
        } catch (IOException e) {
            throw new ExcelParserInputException("Error reading file: " + file, e);
        } catch (LayoutMatchException e) {
            LOG.error("Cannot find layout in file {}", file.toString());
            throw e;
        }
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    // NOTE: expuesto solo en el paquete para testing
    List<SubjectCsvDTO> parseSheet(Sheet sheet) {
        try {
            List<Row> sheetRows = sheet.read();
            Row headerRow = searchHeadersRow(sheetRows);

            if (headerRow == null) {
                return List.of();
            }

            Integer startingRow = headerRow.getRowNum();
            Integer startingCell = calculateStartingCell(headerRow);
            Layout layout = findFittingLayout(headerRow);

            List<SubjectCsvDTO> subjects = new ArrayList<>();
            for (int i = startingRow; i < sheetRows.size(); i++) {
                Row row = sheetRows.get(i);

                if (isEmptyRow(row) || layout.headers.size() > row.getCellCount()) {
                    break;
                }

                SubjectCsvDTO subject = parseRow(row, layout, startingCell);
                subjects.add(subject);
            }

            return subjects;
        } catch (IOException e) {
            throw new ExcelParserInputException("Cannot read sheet: " + sheet.getName(), e);
        }
    }

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
     * @return Objeto SubjectCsvDTO poblado con los datos de la fila.
     * 
     * @throws ClassCastException Si el contenido de alguna celda no puede
     *                            convertirse a String
     * @see Layout
     * @see SubjectCsvDTO
     */
    private static SubjectCsvDTO parseRow(Row rowData, Layout layout, Integer startingCell) {
        SubjectCsvDTO dto = new SubjectCsvDTO();
        int currentCell = startingCell - 1;

        for (String layoutField : layout.headers) {
            currentCell++;
            if (currentCell >= rowData.getCellCount()) {
                break;
            }

            Cell rowCell = rowData.getCell(currentCell);
            if (rowCell == null) {
                continue;
            }

            String cellValue = rowCell.getText();
            BiConsumer<SubjectCsvDTO, String> mapper = fieldMapper.get(layoutField);
            if (mapper != null) {
                mapper.accept(dto, cellValue);
            }
        }

        return dto;
    }

    private static Row searchHeadersRow(List<Row> rows) {
        for (Row row : rows) {
            if (isHeader(row)) {
                return row;
            }
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
    private Layout findFittingLayout(Row r) {
        List<Cell> cells = r.stream().collect(Collectors.toList());

        return layouts.stream()
                .filter(layout -> layoutMatchesRow(layout, cells))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No matching layout found for row: " + r));
    }

    private boolean layoutMatchesRow(Layout layout, List<Cell> cells) {
        int cellIndex = 0;
        int headerIndex = 0;

        while (headerIndex < layout.headers.size() && cellIndex < cells.size()) {
            Cell cell = cells.get(cellIndex);
            String cellValue = (cell != null) ? cell.getText().trim().toLowerCase() : "";
            cellIndex++;

            if (cellValue.isEmpty()) {
                continue;
            }

            String header = layout.headers.get(headerIndex);
            List<String> expectedPatterns = layout.patterns.get(header);

            boolean matches = false;
            for (String pattern : expectedPatterns) {
                if (cellValue.contains(pattern)) {
                    matches = true;
                    break;
                }
            }

            if (!matches) {
                return false;
            }
            headerIndex++;
        }

        return headerIndex == layout.headers.size();
    }

    // =====================================
    // ======== Utility methods ============
    // =====================================

    // Determina la fila del Excel de encabezados buscando la celda que
    // contenga el texto "ítem" o "item" (case insensitive).
    private static boolean isHeader(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getType() == CellType.STRING) {
                String value = ((String) cell.getValue()).toLowerCase().trim();
                if (HEADER_KEYWORDS.contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        for (Cell cell : row) {
            if (cell != null && cell.getValue() != null) {
                String value = cell.getValue().toString().trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Integer calculateStartingCell(Row row) {
        for (int i = 0; i < row.getCellCount(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !cell.getText().trim().isEmpty()) {
                return i;
            }
        }
        return 0;
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
