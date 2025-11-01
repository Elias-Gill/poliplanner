package poliplanner.excel.parser;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import poliplanner.excel.exception.ExcelParserConfigurationException;
import poliplanner.excel.exception.ExcelParserException;
import poliplanner.excel.exception.ExcelParserInputException;
import poliplanner.excel.exception.LayoutMatchException;
import poliplanner.excel.parser.JsonLayoutLoader.Layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ExcelParser {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelParser.class);

    private List<Layout> layouts;
    private static final Set<String> HEADER_KEYWORDS = Set.of("ítem", "item");

    private ReadableWorkbook wb;
    private List<Sheet> sheets;
    private Integer currentSheet = -1;

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

    public void parseExcel(File file) throws ExcelParserException {
        try (InputStream is = new FileInputStream(file)) {
            LOG.info("Parsing file: {}", file.toString());
            this.wb = new ReadableWorkbook(is);
            this.sheets = wb.getSheets().toList();
        } catch (FileNotFoundException e) {
            throw new ExcelParserConfigurationException("File not found: " + file, e);
        } catch (IOException e) {
            throw new ExcelParserInputException("Error reading file: " + file, e);
        } catch (LayoutMatchException e) {
            LOG.error("Cannot find layout in file {}", file.toString());
            throw e;
        }
    }

    public boolean hasSheet() {
        while (true) {
            this.currentSheet++;
            if (this.sheets.size() <= this.currentSheet) {
                return false;
            }

            Sheet sheet = this.sheets.get(this.currentSheet);
            String career = sheet.getName();

            // Ignorar hojas "basura"
            if (career.contains("odigos")
                    || career.contains("ódigos")
                    || career.contains("Asignaturas")
                    || career.contains("Homologadas")
                    || career.contains("Homólogas")
                    || career.equalsIgnoreCase("códigos")) {
                LOG.info("Ignoring sheet: {}", career);
                continue;
            } else {
                return true;
            }
        }
    }

    public ParsingResult parseCurrentSheet() {
        Sheet sheet = this.sheets.get(this.currentSheet);
        String career = sheet.getName();
        ParsingResult result = new ParsingResult();

        LOG.info("Parsing sheet: {}", career);

        List<SubjectcDTO> subjects = parseSheet(sheet);
        result.career = career;
        result.subjects = subjects;

        LOG.info("Sheet parsed succesfully", career);

        return result;
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    public class ParsingResult {
        public String career;
        public List<SubjectcDTO> subjects;
    }

    // NOTE: expuesto solo en el paquete para testing
    List<SubjectcDTO> parseSheet(Sheet sheet) {
        try {
            List<Row> sheetRows = sheet.read();
            Row headerRow = searchHeadersRow(sheetRows);

            if (headerRow == null) {
                return List.of();
            }

            Integer startingRow = headerRow.getRowNum();
            Integer startingCell = calculateStartingCell(headerRow);
            Layout layout = findFittingLayout(headerRow);

            List<SubjectcDTO> subjects = new ArrayList<>();
            for (int i = startingRow; i < sheetRows.size(); i++) {
                Row row = sheetRows.get(i);

                if (isEmptyRow(row) || layout.headers.size() > row.getCellCount()) {
                    break;
                }

                SubjectcDTO subject = parseRow(row, layout, startingCell);
                subjects.add(subject);
            }

            return subjects;
        } catch (IOException e) {
            throw new ExcelParserInputException("Cannot read sheet: " + sheet.getName(), e);
        }
    }

    /**
     * Parsea una fila de Excel y la convierte en un objeto SubjectCsvDTO según el layout
     * especificado.
     *
     * <p>La función recorre cada fila y mapea los encabezados correspondientes del DTO basándose en
     * los encabezados definidos en el layout. Los valores se asignan usando los setters del DTO,
     * aplicando automáticamente sanitización (incluida en dichos setters. Por ejemplo conversión de
     * números, fechas o horas).
     *
     * @param rowData La fila de Excel a parsear (objeto Row de la librería FastExcel)
     * @param layout Objeto Layout que define la estructura y mapeo de los encabezados
     * @param startingCell Número de celda (basado en 1) donde comienzan los datos a parsear
     * @return Objeto SubjectCsvDTO poblado con los datos de la fila, con sanitización aplicada
     * @throws ClassCastException Si el contenido de alguna celda no puede convertirse a String
     * @see Layout
     * @see SubjectcDTO
     */
    private static SubjectcDTO parseRow(Row rowData, Layout layout, Integer startingCell) {
        SubjectcDTO dto = new SubjectcDTO();
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

            // Settear el valor del encabezado en el dto usando setters
            switch (layoutField) {
                // Info general
                case "departamento":
                    dto.setDepartamento(cellValue);
                    break;
                case "asignatura":
                    dto.setNombreAsignatura(cellValue);
                    break;
                case "nivel":
                    dto.setSemestre(cellValue);
                    break;
                case "semestre":
                    dto.setSemestre(cellValue); // usa convertStringToNumber internamente
                    break;
                case "seccion":
                    dto.setSeccion(cellValue);
                    break;

                // Info del docente
                case "titulo":
                    dto.setTituloProfesor(cellValue);
                    break;
                case "apellido":
                    dto.setApellidoProfesor(cellValue);
                    break;
                case "nombre":
                    dto.setNombreProfesor(cellValue);
                    break;
                case "correo":
                    dto.setEmailProfesor(cellValue);
                    break;

                // Primer parcial
                case "diaParcial1":
                    dto.setParcial1Fecha(cellValue);
                    break;
                case "horaParcial1":
                    dto.setParcial1Hora(cellValue);
                    break;
                case "aulaParcial1":
                    dto.setParcial1Aula(cellValue);
                    break;

                // Segundo parcial
                case "diaParcial2":
                    dto.setParcial2Fecha(cellValue);
                    break;
                case "horaParcial2":
                    dto.setParcial2Hora(cellValue);
                    break;
                case "aulaParcial2":
                    dto.setParcial2Aula(cellValue);
                    break;

                // Primer final
                case "diaFinal1":
                    dto.setFinal1Fecha(cellValue);
                    break;
                case "horaFinal1":
                    dto.setFinal1Hora(cellValue);
                    break;
                case "aulaFinal1":
                    dto.setFinal1Aula(cellValue);
                    break;

                // Segundo final
                case "diaFinal2":
                    dto.setFinal2Fecha(cellValue);
                    break;
                case "horaFinal2":
                    dto.setFinal2Hora(cellValue);
                    break;
                case "aulaFinal2":
                    dto.setFinal2Aula(cellValue);
                    break;

                // Revisiones
                case "revisionDia":
                    dto.setFinal1RevFecha(cellValue);
                    dto.setFinal2RevFecha(cellValue);
                    break;
                case "revisionHora":
                    dto.setFinal1RevHora(cellValue);
                    dto.setFinal2RevHora(cellValue);
                    break;

                // Comité
                case "mesaPresidente":
                    dto.setComitePresidente(cellValue);
                    break;
                case "mesaMiembro1":
                    dto.setComiteMiembro1(cellValue);
                    break;
                case "mesaMiembro2":
                    dto.setComiteMiembro2(cellValue);
                    break;

                // Horario semanal
                case "aulaLunes":
                    dto.setAulaLunes(cellValue);
                    break;
                case "horaLunes":
                    dto.setLunes(cellValue);
                    break;
                case "aulaMartes":
                    dto.setAulaMartes(cellValue);
                    break;
                case "horaMartes":
                    dto.setMartes(cellValue);
                    break;
                case "aulaMiercoles":
                    dto.setAulaMiercoles(cellValue);
                    break;
                case "horaMiercoles":
                    dto.setMiercoles(cellValue);
                    break;
                case "aulaJueves":
                    dto.setAulaJueves(cellValue);
                    break;
                case "horaJueves":
                    dto.setJueves(cellValue);
                    break;
                case "aulaViernes":
                    dto.setAulaViernes(cellValue);
                    break;
                case "horaViernes":
                    dto.setViernes(cellValue);
                    break;
                case "aulaSabado":
                    dto.setAulaSabado(cellValue);
                    break;
                case "horaSabado":
                    dto.setSabado(cellValue);
                    break;
                case "fechasSabado":
                    dto.setFechasSabadoNoche(cellValue);
                    break;

                default:
                    // Ignorar campos que no nos importan
                    break;
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
     * Busca y devuelve el primer {@link Layout} cuyo conjunto de encabezados coincida con los
     * valores de las celdas en la fila dada. La comparación de encabezados se hace ignorando
     * mayúsculas y minúsculas, y permitiendo celdas vacías (se ignoran).
     *
     * @param r Fila de entrada cuyas celdas se usan para verificar coincidencias.
     * @return El {@link Layout} que coincide con la fila.
     * @throws IllegalArgumentException Si no se encuentra ningún Layout que coincida.
     */
    private Layout findFittingLayout(Row r) {
        List<Cell> cells = r.stream().collect(Collectors.toList());

        return layouts.stream()
                .filter(layout -> layoutMatchesRow(layout, cells))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
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
}
