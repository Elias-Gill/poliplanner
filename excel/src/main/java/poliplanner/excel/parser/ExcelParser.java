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
import java.util.stream.Collectors;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poliplanner.excel.parser.JsonLayoutLoader.Layout;

@Component
@RequiredArgsConstructor
public class ExcelParser {
    private List<Layout> layouts;

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

    public Map<String, List<SubjectCsvDTO>> parseExcel(Path file) {
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
        }
        return null;
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

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

            // Info general
            if (layoutField.equals("departamento")) {
                dto.departamento = cellValue;
            } else if (layoutField.equals("asignatura")) {
                dto.nombreAsignatura = cellValue;
            } else if (layoutField.equals("nivel")) {
                dto.nivel = cellValue;
            } else if (layoutField.equals("semestre")) {
                dto.semestre = cellValue;
            } else if (layoutField.equals("seccion")) {
                dto.seccion = cellValue;
            }

            // Info del docente
            else if (layoutField.equals("titulo")) {
                dto.tituloProfesor = cellValue;
            } else if (layoutField.equals("apellido")) {
                dto.apellidoProfesor = cellValue;
            } else if (layoutField.equals("nombre")) {
                dto.nombreProfesor = cellValue;
            } else if (layoutField.equals("correo")) {
                dto.emailProfesor = cellValue;
            }

            // Primer parcial
            else if (layoutField.equals("diaParcial1")) {
                dto.parcial1Fecha = cellValue;
            } else if (layoutField.equals("horaParcial1")) {
                dto.parcial1Hora = cellValue;
            } else if (layoutField.equals("aulaParcial1")) {
                dto.parcial1Aula = cellValue;
            }

            // Segundo parcial
            else if (layoutField.equals("diaParcial2")) {
                dto.parcial2Fecha = cellValue;
            } else if (layoutField.equals("horaParcial2")) {
                dto.parcial2Hora = cellValue;
            } else if (layoutField.equals("aulaParcial2")) {
                dto.parcial2Aula = cellValue;
            }

            // Primer Final
            else if (layoutField.equals("diaFinal1")) {
                dto.final1Fecha = cellValue;
            } else if (layoutField.equals("horaFinal1")) {
                dto.final1Hora = cellValue;
            } else if (layoutField.equals("aulaFinal1")) {
                dto.final1Aula = cellValue;
            }

            // Segundo Final
            else if (layoutField.equals("diaFinal2")) {
                dto.final2Fecha = cellValue;
            } else if (layoutField.equals("horaFinal2")) {
                dto.final2Hora = cellValue;
            } else if (layoutField.equals("aulaFinal2")) {
                dto.final2Aula = cellValue;
            }

            // Revisiones
            else if (layoutField.equals("revisionDia")) {
                dto.final1RevFecha = cellValue;
                // Asumiendo que el mismo campo aplica para ambos finales
                dto.final2RevFecha = cellValue;
            } else if (layoutField.equals("revisionHora")) {
                dto.final1RevHora = cellValue;
                // Asumiendo que el mismo campo aplica para ambos finales
                dto.final2RevHora = cellValue;
            }

            // Comité
            else if (layoutField.equals("mesaPresidente")) {
                dto.comitePresidente = cellValue;
            } else if (layoutField.equals("mesaMiembro1")) {
                dto.comiteMiembro1 = cellValue;
            } else if (layoutField.equals("mesaMiembro2")) {
                dto.comiteMiembro2 = cellValue;
            }

            // Horario semanal
            else if (layoutField.equals("aulaLunes")) {
                dto.aulaLunes = cellValue;
            } else if (layoutField.equals("horaLunes")) {
                dto.lunes = cellValue;
            }

            else if (layoutField.equals("aulaMartes")) {
                dto.aulaMartes = cellValue;
            } else if (layoutField.equals("horaMartes")) {
                dto.martes = cellValue;
            }

            else if (layoutField.equals("aulaMiercoles")) {
                dto.aulaMiercoles = cellValue;
            } else if (layoutField.equals("horaMiercoles")) {
                dto.miercoles = cellValue;
            }

            else if (layoutField.equals("aulaJueves")) {
                dto.aulaJueves = cellValue;
            } else if (layoutField.equals("horaJueves")) {
                dto.jueves = cellValue;
            }

            else if (layoutField.equals("aulaViernes")) {
                dto.aulaViernes = cellValue;
            } else if (layoutField.equals("horaViernes")) {
                dto.viernes = cellValue;
            }

            else if (layoutField.equals("aulaSabado")) {
                dto.aulaSabado = cellValue;
            } else if (layoutField.equals("horaSabado")) {
                dto.sabado = cellValue;
            } else if (layoutField.equals("fechasSabado")) {
                dto.fechasSabadoNoche = cellValue;
            }
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

        for (Layout l : layouts) {
            boolean matches = true;
            for (int i = 0; i < l.headers.size(); i++) {
                String expected = l.headers.get(i).trim();
                String actual = getStringCellValueSafe(cells, i).trim();
                if (actual.isEmpty()) {
                    continue;
                }

                if (!expected.equalsIgnoreCase(actual)) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                return l;
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
        return cells.get(index).getText();
    }

    // Determina la fila del Excel de encabezados buscando la celda que
    // contenga el texto "ítem" o "item" (case insensitive).
    private static boolean isHeader(Row r) {
        // Implementación con Stream que:
        // 1. Filtra solo celdas de tipo STRING (not null)
        // 2. Normaliza el contenido (minúsculas y sin espacios)
        // 3. Early return al encontrar una coincidencia
        return r.stream()
                .filter(cell -> (cell != null && cell.getType() == CellType.STRING))
                .map(cell -> ((String) cell.getValue()).toLowerCase().trim())
                .anyMatch(value -> value.contains("ítem") || value.contains("item"));
    }

    private static boolean isEmptyRow(Row r) {
        return r.stream().allMatch(
                cell -> cell == null || cell.getValue() == null || cell.getValue().toString().trim().isEmpty());
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
}
