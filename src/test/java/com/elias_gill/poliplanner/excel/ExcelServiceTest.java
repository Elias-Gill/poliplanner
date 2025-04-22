package com.elias_gill.poliplanner.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.elias_gill.poliplanner.excel.dto.SubjectCsv;

public class ExcelServiceTest {

    @Test
    @Tag("unit")
    void testCleanCsv() throws Exception {
        // Preparar archivo temporal copiando el input de prueba
        Path inputPath = Path.of("src/test/resources/input.csv");
        Path tempFile = Files.createTempFile("test-clean", ".csv");
        Files.copy(inputPath, tempFile, StandardCopyOption.REPLACE_EXISTING);

        // Ejecutar el método a testear
        ExcelService.cleanCsv(tempFile);

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
        List<SubjectCsv> subjects = ExcelService.extractSubjects(testFile.toPath());

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
}
