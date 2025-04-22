package com.elias_gill.poliplanner.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class ExcelIntegrationTest {

    @Test
    void testExcelDownload() throws Exception {
        // Tratar de descargar un excel cualquiera y ver si es que funciona
        // correctamente
        try {
            Path file = ExcelService.downloadFile(
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
    void testExcelToCsvConversion() throws Exception {
        if (!ExcelService.isSsconvertAvailable()) {
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
        List<Path> csvFiles = ExcelService.convertExcelToCsv(excelFile);

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
}
