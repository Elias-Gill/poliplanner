package poliplanner.excel.parser;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    void testCleanCsv() throws Exception {
        Map<String, List<SubjectCsvDTO>> materias = new ExcelParser().parseExcel(Path.of(
                "/home/elias/Descargas/excel/Planificación de clases y examenes .Segundo Academico 2025version web180725.xlsx"));

        // Imprimir estructura completa
        System.out.println("=== RESUMEN DE MATERIAS POR DEPARTAMENTO ===");
        System.out.println("Total de departamentos: " + materias.size());

        materias.forEach((departamento, listaMaterias) -> {
            System.out.println("\n--- Departamento: " + departamento + " ---");
            System.out.println("Cantidad de materias: " + listaMaterias.size());

            listaMaterias.forEach(materia -> {
                System.out.println("\n* " + materia.nombreAsignatura + " (" + materia.nivel + ")");
                System.out.println("  - Semestre: " + materia.semestre);
                System.out.println("  - Sección: " + materia.seccion);
                System.out.println("  - Docente: " + materia.tituloProfesor + " " +
                        materia.apellidoProfesor + ", " + materia.nombreProfesor);

                // Imprimir horarios si existen
                if (materia.lunes != null && !materia.lunes.isEmpty()) {
                    System.out.println("\n  Horario:");
                    System.out.println("  - Lunes: " + materia.lunes + " (" + materia.aulaLunes + ")");
                    if (materia.martes != null)
                        System.out.println("  - Martes: " + materia.martes + " (" + materia.aulaMartes + ")");
                    if (materia.miercoles != null)
                        System.out.println("  - Miércoles: " + materia.miercoles + " (" + materia.aulaMiercoles + ")");
                    if (materia.jueves != null)
                        System.out.println("  - Jueves: " + materia.jueves + " (" + materia.aulaJueves + ")");
                    if (materia.viernes != null)
                        System.out.println("  - Viernes: " + materia.viernes + " (" + materia.aulaViernes + ")");
                    if (materia.sabado != null)
                        System.out.println("  - Sábado: " + materia.sabado + " (" + materia.aulaSabado + ")");
                }
            });
        });

        System.out.println("\n=== FIN DEL REPORTE ===");
    }
}
