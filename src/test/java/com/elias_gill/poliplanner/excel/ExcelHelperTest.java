package com.elias_gill.poliplanner.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.elias_gill.poliplanner.excel.dto.SubjectCsv;

public class ExcelHelperTest {
    @Test
    @Tag("unit")
    void testCleanCsv() throws Exception {
        // Preparar archivo temporal copiando el input de prueba
        Path inputPath = Path.of("src/test/resources/input.csv");
        Path tempFile = Files.createTempFile("test-clean", ".csv");
        Files.copy(inputPath, tempFile, StandardCopyOption.REPLACE_EXISTING);

        // Ejecutar el método a testear
        ExcelHelper.cleanCsv(tempFile);

        // Comparar con el output esperado
        Path expectedPath = Path.of("src/test/resources/output.csv");
        List<String> cleanedLines = Files.readAllLines(tempFile);
        List<String> expectedLines = Files.readAllLines(expectedPath);

        assertEquals(expectedLines.size(), cleanedLines.size(),
                "El número de líneas no coincide");

        for (int i = 0; i < expectedLines.size(); i++) {
            assertEquals(expectedLines.get(i), cleanedLines.get(i),
                    "Línea " + (i + 1) + " no coincide");
        }

        // Limpieza
        Files.deleteIfExists(tempFile);
    }

    @Test
    @Tag("unit")
    void testExtractSubjects() throws Exception {
        // Archivo de prueba ya sanitizado
        File testFile = new File("src/test/resources/output.csv");

        // Ejecutar el método
        List<SubjectCsv> subjects = ExcelHelper.extractSubjects(testFile.toPath());

        // Verificar resultados
        assertNotNull(subjects, "La lista de subjects no debería ser null");
        assertFalse(subjects.isEmpty(), "La lista de subjects no debería estar vacía");

        // Verificar primera entrada
        SubjectCsv first = subjects.get(0);
        assertEquals("DCB", first.departamento);
        assertEquals("Algebra Lineal", first.nombreAsignatura);
        assertEquals("2", first.semestre);
        assertEquals("MI", first.seccion);
        assertEquals("", first.tituloProfesor);
        assertEquals("A CONFIRMAR", first.apellidoProfesor);
        assertEquals("", first.nombreProfesor);
        assertEquals("", first.emailProfesor);
        assertEquals("F16", first.aulaViernes);

        // Verificar última entrada
        SubjectCsv last = subjects.get(subjects.size() - 1);
        assertEquals("DG", last.departamento);
        assertEquals("Técnicas de Organización y Métodos (*)", last.nombreAsignatura);
        assertEquals("5", last.semestre);
        assertEquals("TR", last.seccion);
        assertEquals("Lic.", last.tituloProfesor);
        assertEquals("Demattei Ortiz", last.apellidoProfesor);
        assertEquals("Zulma Lucía", last.nombreProfesor);
        assertEquals("zdematei@pol.una.py", last.emailProfesor);
        assertEquals("18:00:00", last.final1Hora);

        // Parsear la fecha esperada (Jue 19/06/25)
        LocalDate fechaEsperada = LocalDate.of(2025, 6, 19);
        assertEquals(fechaEsperada, last.final1Fecha);

        assertEquals(null, last.parcial1Fecha);
        assertEquals("Lic. Zulma Lucía Demattei Ortiz", last.comitePresidente);
        assertEquals("F16", last.final1Aula);
        assertEquals("Ms. Estela Mary Ramírez Barboza", last.comiteMiembro1);
        assertEquals("Lic. Osvaldo David Sosa Cabrera", last.comiteMiembro2);
    }

    @Test
    @Tag("integration")
    void testExcelDownload() throws Exception {
        // Tratar de descargar un excel cualquiera y ver si es que funciona
        // correctamente
        try {
            Path file = ExcelHelper.downloadFile(
                    "https://www.pol.una.py/wp-content/uploads/Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx");

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
                "Cnel. Oviedo.csv", "IAE.csv", "ICM.csv", "IEK.csv",
                "IEL.csv", "IEN.csv", "IIN.csv", "IMK.csv", "ISP.csv", "LCA.csv",
                "LCI.csv", "LCIk.csv", "LEL.csv", "LGH.csv", "TSE.csv", "Villarrica.csv");

        // Extraer nombres reales
        Set<String> nombresGenerados = csvFiles.stream()
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toSet());

        assertEquals(nombresEsperados, nombresGenerados, "Los archivos generados no coinciden con los esperados");
    }

    @Test
    @Tag("unit")
    void testFindLatestExcelUrlFromLocalHtml() throws IOException {
        Path htmlPath = Path.of("src/test/resources/pagina_facultad.html");
        String htmlContent = Files.readString(htmlPath);

        Document doc = Jsoup.parse(htmlContent);
        String latestUrl = ExcelHelper.extractUrlFromDoc(doc);

        assertEquals(
                "https://www.pol.una.py/wp-content/uploads/Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx",
                latestUrl);
    }

    // TODO: test de que se carguen a la base de datos correctamente
}
