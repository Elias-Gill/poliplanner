package poliplanner.excel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExcelParserTest {
    private static final String SRC_TEST_RESOURCES_LAYOUT = "/con_aulas__con_revision.json";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Tag("unit")
    void testParseFromJsonResource() throws Exception {
        Map<String, List<SubjectCsvDTO>> result = new ExcelParser(new JsonLayoutLoader()).parseSheet();

        // Validaciones generales
        assertNotNull(result, "El mapa de resultados no debería ser null");
        assertFalse(result.isEmpty(), "El mapa de resultados no debería estar vacío");

        // Unir todas las listas de subjects
        List<SubjectCsvDTO> subjects = result.values().stream()
                .flatMap(List::stream)
                .toList();

        assertFalse(subjects.isEmpty(), "La lista total de subjects no debería estar vacía");

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

    /*
     * @Test
     * 
     * @Tag("integration")
     * void testExcelToCsvConversion() throws Exception {
     * if (!ExcelParser.isSsconvertAvailable()) {
     * System.out.println("Test ignorado. 'Gnumeric' no instalado");
     * assumeTrue(false);
     * }
     * 
     * // Cargar el archivo desde resources
     * Path excelFile;
     * try (InputStream in =
     * getClass().getClassLoader().getResourceAsStream("testExcel.xlsx")) {
     * assertNotNull(in, "No se encontró el archivo 'testExcel.xlsx' en resources");
     * excelFile = Files.createTempFile("testExcel", ".xlsx");
     * Files.copy(in, excelFile, StandardCopyOption.REPLACE_EXISTING);
     * }
     * 
     * // Ejecutar la conversión
     * List<Path> csvFiles = ExcelParser.convertExcelToCsv(excelFile);
     * 
     * // Esperados
     * Set<String> nombresEsperados = Set.of(
     * "Cnel. Oviedo.csv", // NOTE: en algunas versiones del horario es super
     * inconsistente
     * "Villarrica.csv", // tambien es inconsistente
     * "IAE.csv",
     * "ICM.csv",
     * "IEK.csv",
     * "IEL.csv",
     * "IEN.csv",
     * "IIN.csv",
     * "IMK.csv",
     * "ISP.csv",
     * "LCA.csv",
     * "LCI.csv",
     * "LCIk.csv",
     * "LEL.csv",
     * "LGH.csv",
     * "TSE.csv");
     * 
     * // Extraer nombres reales
     * Set<String> nombresGenerados = csvFiles.stream().map(p ->
     * p.getFileName().toString())
     * .collect(Collectors.toSet());
     * 
     * assertEquals(
     * nombresEsperados,
     * nombresGenerados,
     * "Los archivos generados no coinciden con los esperados");
     * 
     * // Cargar el archivo IIN.csv generado y el archivo input.csv de resources
     * para
     * // comparar
     * Path iinCsvGenerated = csvFiles.stream()
     * .filter(p -> p.getFileName().toString().equals("IIN.csv"))
     * .findFirst()
     * .orElseThrow(() -> new AssertionError("No se generó el archivo IIN.csv"));
     * 
     * Path inputCsvExpected = Path.of(SRC_TEST_RESOURCES_INPUT_CSV);
     * 
     * // Comparar el contenido de ambos archivos
     * List<String> generatedLines = Files.readAllLines(iinCsvGenerated);
     * List<String> expectedLines = Files.readAllLines(inputCsvExpected);
     * 
     * for (int i = 0; i < generatedLines.size(); i++) {
     * assertEquals(
     * expectedLines.get(i),
     * generatedLines.get(i),
     * "Las lineas '"
     * + i
     * + "' no son iguales: \n"
     * + expectedLines.get(i)
     * + "\n\n"
     * + generatedLines.get(i)
     * + "\n\n");
     * }
     * }
     */
}
