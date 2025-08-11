package poliplanner.excel.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

            for (Sheet s : wb.getSheets().toList()) {
                // Ignorar hojas basura
                String career = s.getName();
                if (career.contains("odigos")
                        || career.contains("ódigos")
                        || career.contains("Asignaturas")
                        || career.contains("Homologadas")
                        || career.contains("Homólogas")
                        || career.equalsIgnoreCase("códigos")) {
                    continue;
                }

                Row headerRow = searchHeadersRow(s);
                Layout layout = findFittingLayout(headerRow);

                // TODO: parsear materias el layout encontrado
            }

        } catch (Exception e) {
        }
        return null;
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    // Debe retornar la columna de inicio del headers row (item)
    private static Integer parseHeadersRow(Row row) {
        // TODO: continuar
        return 1;
    }

    private static Row searchHeadersRow(Sheet sheet) throws FileNotFoundException, IOException {
        for (Row r : sheet.read()) {
            if (r.getCellCount() < 0 || !isHeader(r)) {
                continue;
            }
            return r;
        }

        return null;
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

    private String getStringCellValueSafe(List<Cell> cells, int index) {
        if (index >= cells.size() || cells.get(index) == null) {
            return "";
        }
        return cells.get(index).getText();
    }

    // =====================================
    // ======== Utility methods ============
    // =====================================
}
