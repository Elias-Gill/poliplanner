package poliplanner.excel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.util.List;

import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Sheet;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.ClassPathResource;

public class ExcelParserTest {
    final private ClassPathResource CLASSPATH_TEST_EXCEL = new ClassPathResource("testExcel.xlsx");

    @Test
    void testParseSheet() throws Exception {
        try (InputStream is = CLASSPATH_TEST_EXCEL.getInputStream();
                ReadableWorkbook wb = new ReadableWorkbook(is)) {
            List<Sheet> sheets = wb.getSheets().toList();

            Sheet sheet = null;
            for (Sheet s : sheets) {
                if (s.getName().equals("IIN")) {
                    sheet = s;
                    break;
                }
            }

            if (sheet == null) {
                throw new RuntimeException("Cannot find 'IIN' sheet inside testExcel.xlsx");
            }

            List<SubjectCsvDTO> subjects = new ExcelParser().parseSheet(sheet);

            assertNotNull(subjects, "La lista de materias no debería estar vacía");
            assertFalse(subjects.isEmpty(), "La lista de materias no debería estar vacía");

            // Primera entrada
            SubjectCsvDTO first = subjects.get(0);
            assertEquals("DCB", first.departamento);
            assertEquals("Algebra Lineal", first.nombreAsignatura);
            assertEquals("2", first.semestre);
            assertEquals("MI", first.seccion);
            assertEquals("Lic.", first.tituloProfesor);
            assertEquals("Villasanti Flores", first.apellidoProfesor);
            assertEquals("Richard Adrián", first.nombreProfesor);
            assertEquals("", first.emailProfesor);

            // Última entrada
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
    }
}
