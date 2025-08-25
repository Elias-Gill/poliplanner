package poliplanner.test.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestConstructor;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import poliplanner.excel.ExcelService;
import poliplanner.excel.sources.ExcelDownloadSource;
import poliplanner.models.Career;
import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;
import poliplanner.repositories.CareerRepository;
import poliplanner.repositories.SheetVersionRepository;
import poliplanner.repositories.SubjectRepository;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Rollback
@RequiredArgsConstructor
class ExcelServiceIntegrationTest {
    final private ExcelService excelService;
    final private SheetVersionRepository sheetVersionRepository;
    final private CareerRepository careerRepository;
    final private SubjectRepository subjectRepository;

    private static final String EXCEL_DOWNLOAD_URL = "https://www.pol.una.py/wp-content/uploads/Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx";

    @Test
    void testExcelDownload() throws Exception {
        // Tratar de descargar un excel conocido y ver si es que funciona
        // correctamente
        try {
            ExcelDownloadSource source = new ExcelDownloadSource(EXCEL_DOWNLOAD_URL, "test_file", LocalDate.now());
            File file = source.downloadThisSource();

            assertNotNull(file);
            assertTrue(file.exists());
            assertTrue(file.length() > 0);
        } catch (Exception e) {
            System.out.println("Test ignorado. No se puede conectar con el url de descarga");
            assumeTrue(false);
        }
    }

    /**
     * Test de integración que verifica el proceso completo de parseo de un archivo
     * Excel y la
     * persistencia de sus datos en la base de datos.
     *
     * <p>
     * Flujo verificado: 1. Lectura y parseo del archivo Excel. 2. Persistencia de
     * la versión del
     * archivo (SheetVersion) con la URL asociada. 3. Creacion y persistencia de
     * entidades Career y
     * Subject a partir del contenido del Excel.
     *
     * <p>
     * Este test asume que el archivo testeado contiene datos validos y
     * estructurados
     * correctamente.
     */
    @Test
    void testExcelParsingPersistency() throws Exception {
        File testExcel = Paths.get("src/test/resources/testExcel.xlsx").toFile();
        String dummyUrl = "URL";

        excelService.parseAndPersistExcel(testExcel, dummyUrl);

        // Verificamos que se haya creado la version
        SheetVersion version = sheetVersionRepository.findFirstByOrderByParsedAtDescIdDesc();
        assertEquals(dummyUrl, version.getUrl());

        // Verificamos que se hayan creado carreras
        List<Career> careers = careerRepository.findAll();
        assert (!careers.isEmpty());

        // Verificamos que se hayan creado materias
        List<Subject> subjects = subjectRepository.findAll();
        assert (!subjects.isEmpty());

        // Buscar si quedaron materias con semestre desconocido
        Set<Subject> problematicSubjects = subjects.stream()
                .filter(entry -> entry.getSemestre() == 0)
                .collect(Collectors.toSet());

        if (!problematicSubjects.isEmpty()) {
            System.err.println("Algunas materias no cuentan con semestre definido correctamente");
        }
    }
}
