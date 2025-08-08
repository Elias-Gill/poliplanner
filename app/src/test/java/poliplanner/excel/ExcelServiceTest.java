package poliplanner.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import poliplanner.models.Career;
import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;
import poliplanner.repositories.CareerRepository;
import poliplanner.repositories.SheetVersionRepository;
import poliplanner.repositories.SubjectRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback
class ExcelServiceIntegrationTest {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private SheetVersionRepository sheetVersionRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // @Test
    // @Tag("integration")
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
    /* void testExcelParsingPersistency() throws Exception {
        Path testExcel = Paths.get("src/test/resources/testExcel.xlsx");
        String dummyUrl = "URL";

        excelService.persistSubjectsFromExcel(testExcel, dummyUrl);

        // Verificamos que se haya creado la version
        SheetVersion version = sheetVersionRepository.findFirstByOrderByParsedAtDesc();
        assertEquals(dummyUrl, version.getUrl());

        // Verificamos que se hayan creado carreras
        List<Career> careers = careerRepository.findAll();
        assert (!careers.isEmpty());

        // Verificamos que se hayan creado materias
        List<Subject> subjects = subjectRepository.findAll();
        assert (!subjects.isEmpty());
    } */
}
