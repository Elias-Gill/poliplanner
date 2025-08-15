package poliplanner.excel.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class ParserTest {
    final private File CLASSPATH_TEST_EXCEL;

    ParserTest() throws IOException {
        this.CLASSPATH_TEST_EXCEL = new ClassPathResource("testExcel.xlsx").getFile();
    }

    @Test
    void testE2EParsing() throws Exception {
        ExcelParser parser = new ExcelParser();
        assertDoesNotThrow(() -> {
            // Verifica que no se lance ninguna excepción al parsear
            parser.parseExcel(CLASSPATH_TEST_EXCEL);
        }, "El parser debería procesar el archivo sin lanzar excepciones");
    }
}
