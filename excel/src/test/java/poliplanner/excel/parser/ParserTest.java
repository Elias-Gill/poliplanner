package poliplanner.excel.parser;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    void testCleanCsv() throws Exception {
        ExcelParser.parseExcel(Path.of(
                "/home/elias/Descargas/excel/Planificación de clases y examenes .Segundo Academico 2025version web180725.xlsx"));
    }
}
