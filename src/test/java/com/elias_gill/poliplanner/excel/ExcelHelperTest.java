package com.elias_gill.poliplanner.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExcelHelperTest {
    private static final String EXCEL_DOWNLOAD_URL = "https://www.pol.una.py/wp-content/uploads/Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx";
    private static final String SRC_TEST_RESOURCES_INPUT_CSV = "src/test/resources/input.csv";
    private static final String SRC_TEST_RESOURCES_OUTPUT_CSV = "src/test/resources/output.csv";

    @Test
    @Tag("unit")
    void testCleanCsv() throws Exception {
        Path tempFile = ExcelHelper.cleanCsv(Path.of(SRC_TEST_RESOURCES_INPUT_CSV));

        // Comparar con el output esperado
        Path expectedPath = Path.of(SRC_TEST_RESOURCES_OUTPUT_CSV);
        List<String> cleanedLines = Files.readAllLines(tempFile);
        List<String> expectedLines = Files.readAllLines(expectedPath);

        assertEquals(expectedLines.size(), cleanedLines.size(), "El número de líneas no coincide");

        for (int i = 0; i < expectedLines.size(); i++) {
            assertEquals(
                    expectedLines.get(i), cleanedLines.get(i), "Línea " + (i + 1) + " no coincide");
        }

        // Limpieza
        Files.deleteIfExists(tempFile);
    }

    @Test
    @Tag("unit")
    void testExtractSubjects() throws Exception {
        // Archivo de prueba ya sanitizado
        File testFile = new File(SRC_TEST_RESOURCES_OUTPUT_CSV);

        // Ejecutar el método
        List<SubjectCsvDTO> subjects = ExcelHelper.extractSubjects(testFile.toPath());

        // Verificar resultados
        assertNotNull(subjects, "La lista de subjects no debería ser null");
        assertFalse(subjects.isEmpty(), "La lista de subjects no debería estar vacía");

        // Verificar primera entrada
        SubjectCsvDTO first = subjects.get(0);
        assertEquals("DCB", first.departamento);
        assertEquals("Algebra Lineal", first.nombreAsignatura);
        assertEquals("2", first.semestre);
        assertEquals("MI", first.seccion);
        assertEquals("Lic.", first.tituloProfesor);
        assertEquals("Villasanti Flores", first.apellidoProfesor);
        assertEquals("Richard Adrián", first.nombreProfesor);
        assertEquals("", first.emailProfesor);

        // Verificar última entrada
        SubjectCsvDTO last = subjects.get(subjects.size() - 1);

        assertEquals("DG", last.departamento);
        assertEquals("Técnicas de Organización y Métodos", last.nombreAsignatura);
        assertEquals("5", last.semestre);
        assertEquals("NA", last.seccion);
        assertEquals("Ms.", last.tituloProfesor);
        assertEquals("Ramírez Barboza", last.apellidoProfesor);
        assertEquals("Estela Mary", last.nombreProfesor);
        assertEquals("emramirez@pol.una.py", last.emailProfesor);

        assertEquals("Mar 17/09/24", last.parcial1Fecha);

        assertEquals("Ms. Estela Mary Ramírez Barboza", last.comitePresidente);
        assertEquals("Lic. Zulma Lucía Demattei Ortíz", last.comiteMiembro1);
        assertEquals("Lic. Osvaldo David Sosa Cabrera", last.comiteMiembro2);

        assertEquals("E01", last.aulaMartes);
        assertEquals("20:45 - 22:15", last.martes);

        assertEquals("E01", last.aulaJueves);
        assertEquals("19:00 - 20:30", last.jueves);

        assertEquals("E01", last.aulaSabado);
        assertEquals("07:30 - 11:30", last.sabado);

        assertEquals("05/10, 23/11", last.fechasSabadoNoche);

        assertEquals("", last.aulaMiercoles);
    }

    @Test
    @Tag("integration")
    void testExcelDownload() throws Exception {
        // Tratar de descargar un excel conocido y ver si es que funciona
        // correctamente
        try {
            Path file = ExcelHelper.downloadFile(EXCEL_DOWNLOAD_URL);

            assertNotNull(file);
            assertTrue(Files.exists(file));
            assertTrue(Files.size(file) > 0);
        } catch (Exception e) {
            System.out.println("Test ignorado. No se puede conectar con el url de descarga");
            assumeTrue(false);
        }
    }

    @Test
    @Tag("integration")
    void testExcelToCsvConversion() throws Exception {
        if (!ExcelHelper.isSsconvertAvailable()) {
            System.out.println("Test ignorado. 'Gnumeric' no instalado");
            assumeTrue(false);
        }

        // Cargar el archivo desde resources
        Path excelFile;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("testExcel.xlsx")) {
            assertNotNull(in, "No se encontró el archivo 'testExcel.xlsx' en resources");
            excelFile = Files.createTempFile("testExcel", ".xlsx");
            Files.copy(in, excelFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // Ejecutar la conversión
        List<Path> csvFiles = ExcelHelper.convertExcelToCsv(excelFile);

        // Esperados
        Set<String> nombresEsperados = Set.of(
                "Cnel. Oviedo.csv", // NOTE: en algunas versiones del horario es super inconsistente
                "Villarrica.csv", // tambien es inconsistente
                "IAE.csv",
                "ICM.csv",
                "IEK.csv",
                "IEL.csv",
                "IEN.csv",
                "IIN.csv",
                "IMK.csv",
                "ISP.csv",
                "LCA.csv",
                "LCI.csv",
                "LCIk.csv",
                "LEL.csv",
                "LGH.csv",
                "TSE.csv");

        // Extraer nombres reales
        Set<String> nombresGenerados = csvFiles.stream().map(p -> p.getFileName().toString())
                .collect(Collectors.toSet());

        assertEquals(
                nombresEsperados,
                nombresGenerados,
                "Los archivos generados no coinciden con los esperados");

        // Cargar el archivo IIN.csv generado y el archivo input.csv de resources para
        // comparar
        Path iinCsvGenerated = csvFiles.stream()
                .filter(p -> p.getFileName().toString().equals("IIN.csv"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se generó el archivo IIN.csv"));

        Path inputCsvExpected = Path.of(SRC_TEST_RESOURCES_INPUT_CSV);

        // Comparar el contenido de ambos archivos
        List<String> generatedLines = Files.readAllLines(iinCsvGenerated);
        List<String> expectedLines = Files.readAllLines(inputCsvExpected);

        for (int i = 0; i < generatedLines.size(); i++) {
            assertEquals(
                    expectedLines.get(i),
                    generatedLines.get(i),
                    "Las lineas '"
                            + i
                            + "' no son iguales: \n"
                            + expectedLines.get(i)
                            + "\n\n"
                            + generatedLines.get(i)
                            + "\n\n");
        }
    }

    @Test
    @Tag("unit")
    void testFindLatestExcelUrlFromLocalHtml() throws IOException {
        Path htmlPath = Path.of("src/test/resources/pagina_facultad.html");
        String htmlContent = Files.readString(htmlPath);

        Document doc = Jsoup.parse(htmlContent);
        String latestUrl = ExcelHelper.extractUrlFromDoc(doc);

        assertEquals(EXCEL_DOWNLOAD_URL, latestUrl);
    }
}
