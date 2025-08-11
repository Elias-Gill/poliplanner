package poliplanner.excel.parser;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import poliplanner.excel.parser.JsonLayoutLoader.Layout;

public class ParserTest {
    @Test
    void testCleanCsv() throws Exception {
        Map<String, List<SubjectCsvDTO>> materias = new ExcelParser(newLayout()).parseExcel(Path.of(
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

    private List<Layout> newLayout() {
        // Crear el layout con todos los campos en el orden correcto
        Layout layout1 = new Layout(
                "planificacion_layout", // fileName
                Arrays.asList(
                        "item", "departamento", "asignatura", "nivel", "semestre", "carrera", "enfasis",
                        "plan", "turno", "seccion", "plataforma", "titulo", "apellido", "nombre", "correo",
                        "diaParcial1", "horaParcial1", "aulaParcial1", "diaParcial2", "horaParcial2", "aulaParcial2",
                        "diaFinal1", "horaFinal1", "aulaFinal1", "revisionFinal1Dia", "revisionFinal1Hora", "diaFinal2",
                        "horaFinal2", "aulaFinal2", "revisionFinal2Dia", "revisionFinal2Hora", "mesaPresidente",
                        "mesaMiembro1", "mesaMiembro2", "aulaLunes", "horaLunes", "aulaMartes", "horaMartes",
                        "aulaMiercoles", "horaMiercoles",
                        "aulaJueves", "horaJueves", "aulaViernes", "horaViernes", "aulaSabado", "horaSabado",
                        "fechasSabado"),
                createPatternsMap());

        Layout layout2 = new Layout(
                "planificacion_layout", // fileName
                Arrays.asList(
                        "item", "departamento", "asignatura", "nivel", "semestre", "carrera", "enfasis",
                        "plan", "turno", "seccion", "plataforma", "titulo", "apellido", "nombre", "correo",
                        "diaParcial1", "horaParcial1", "diaParcial2", "horaParcial2",
                        "diaFinal1", "horaFinal1", "revisionFinal1Dia", "revisionFinal1Hora", "diaFinal2",
                        "horaFinal2", "revisionFinal2Dia", "revisionFinal2Hora", "mesaPresidente",
                        "mesaMiembro1", "mesaMiembro2", "horaLunes", "horaMartes", "horaMiercoles", "horaJueves",
                        "horaViernes", "horaSabado"),
                createPatternsMap());

        return Arrays.asList(layout1, layout2);
    }

    private static Map<String, List<String>> createPatternsMap() {
        Map<String, List<String>> patterns = new HashMap<>();

        // Info general
        patterns.put("item", Arrays.asList("item", "ítem"));
        patterns.put("departamento", Arrays.asList("dpto", "dpto.", "departamento"));
        patterns.put("asignatura", Arrays.asList("asignatura", "materia", "curso"));
        patterns.put("nivel", Arrays.asList("nivel", "correlativas"));
        patterns.put("semestre", Arrays.asList("sem/grupo", "semestre", "grupo"));
        patterns.put("carrera", Arrays.asList("sigla carrera", "carrera", "sigla"));
        patterns.put("enfasis", Arrays.asList("enfasis", "énfasis", "especialidad"));
        patterns.put("plan", Arrays.asList("plan", "programa"));
        patterns.put("turno", Arrays.asList("turno", "horario"));
        patterns.put("seccion", Arrays.asList("sección", "seccion", "grupo"));
        patterns.put("plataforma", Arrays.asList("plataforma de aula virtual", "aula virtual", "plataforma"));

        // Info docente
        patterns.put("titulo", Arrays.asList("tít", "título", "titulo", "tit"));
        patterns.put("apellido", Arrays.asList("apellido", "apellido profesor"));
        patterns.put("nombre", Arrays.asList("nombre", "nombre profesor"));
        patterns.put("correo", Arrays.asList("correo institucional", "email", "correo", "mail"));

        // Parciales
        patterns.put("diaParcial1", Arrays.asList("día", "dia", "fecha"));
        patterns.put("horaParcial1", Arrays.asList("hora", "horario"));
        patterns.put("aulaParcial1", Arrays.asList("aula", "salón", "salon"));

        patterns.put("diaParcial2", Arrays.asList("día", "dia", "fecha"));
        patterns.put("horaParcial2", Arrays.asList("hora", "horario"));
        patterns.put("aulaParcial2", Arrays.asList("aula", "salón", "salon"));

        // Finales
        patterns.put("diaFinal1", Arrays.asList("día", "dia", "fecha"));
        patterns.put("horaFinal1", Arrays.asList("hora", "horario"));
        patterns.put("aulaFinal1", Arrays.asList("aula", "salón", "salon"));
        patterns.put("revisionFinal1Hora", Arrays.asList("hora"));
        patterns.put("revisionFinal1Dia", Arrays.asList("dia", "día"));

        patterns.put("diaFinal2", Arrays.asList("día", "dia", "fecha"));
        patterns.put("horaFinal2", Arrays.asList("hora", "horario"));
        patterns.put("aulaFinal2", Arrays.asList("aula", "salón", "salon"));
        patterns.put("revisionFinal2Hora", Arrays.asList("hora"));
        patterns.put("revisionFinal2Dia", Arrays.asList("dia", "día"));

        // Revisión y comité evaluador
        patterns.put("revisionDia", Arrays.asList("dia", "día", "fecha revisión"));
        patterns.put("revisionHora", Arrays.asList("hora revisión", "hora"));
        patterns.put("mesaPresidente", Arrays.asList("presidente", "presidente comité"));
        patterns.put("mesaMiembro1", Arrays.asList("miembro", "miembro 1", "primer miembro"));
        patterns.put("mesaMiembro2", Arrays.asList("miembro", "miembro 2", "segundo miembro"));

        // Horario semanal
        patterns.put("aulaLunes", Arrays.asList("aula lunes", "aula"));
        patterns.put("horaLunes", Arrays.asList("lunes", "horario lunes"));

        patterns.put("aulaMartes", Arrays.asList("aula martes", "aula"));
        patterns.put("horaMartes", Arrays.asList("martes", "horario martes"));

        patterns.put("aulaMiercoles", Arrays.asList("aula miércoles", "aula miercoles", "aula"));
        patterns.put("horaMiercoles", Arrays.asList("miércoles", "miercoles", "horario miércoles"));

        patterns.put("aulaJueves", Arrays.asList("aula jueves", "aula"));
        patterns.put("horaJueves", Arrays.asList("jueves", "horario jueves"));

        patterns.put("aulaViernes", Arrays.asList("aula viernes", "aula"));
        patterns.put("horaViernes", Arrays.asList("viernes", "horario viernes"));

        patterns.put("aulaSabado", Arrays.asList("aula sábado", "aula sabado", "aula"));
        patterns.put("horaSabado", Arrays.asList("sábado", "sabado", "horario sábado"));
        patterns.put("fechasSabado", Arrays.asList(
                "fechas de clases de sábados (turno noche)",
                "sábado noche",
                "sabado noche",
                "fechas sábado",
                "fechas sabado"));

        return patterns;
    }
}
