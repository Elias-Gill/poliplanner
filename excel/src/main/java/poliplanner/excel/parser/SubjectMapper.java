package poliplanner.excel.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import poliplanner.models.Subject;

@Component
public class SubjectMapper {
    public static Subject mapToSubject(SubjectCsvDTO subjectCsv) {
        if (subjectCsv == null) {
            return null;
        }

        Subject subject = new Subject();
        // informacion general de la materia
        subject.setDepartamento(subjectCsv.departamento);
        subject.setNombreAsignatura(subjectCsv.nombreAsignatura);

        // NOTE: en algunas materias y algunas carreras, no se tiene la informacion del
        // semestre. Si este resulta el caso, entonces se pone como "0". El ExcelService
        // luego se encargara de hacer la desambiguacion de semestres usando la malla
        // cargada dentro de las migraciones.
        subject.setSemestre(convertStringToNumber(subjectCsv.semestre));

        // informacion del profesor
        subject.setSeccion(subjectCsv.seccion);
        subject.setTituloProfesor(subjectCsv.tituloProfesor);
        subject.setApellidoProfesor(subjectCsv.apellidoProfesor);
        subject.setNombreProfesor(subjectCsv.nombreProfesor);
        subject.setEmailProfesor(subjectCsv.emailProfesor);

        // horario de la semana
        subject.setLunes(subjectCsv.lunes);
        subject.setMartes(subjectCsv.martes);
        subject.setMiercoles(subjectCsv.miercoles);
        subject.setJueves(subjectCsv.jueves);
        subject.setViernes(subjectCsv.viernes);
        subject.setSabado(subjectCsv.sabado);

        // aulas de clase
        subject.setAulaLunes(subjectCsv.aulaLunes);
        subject.setAulaMartes(subjectCsv.aulaMartes);
        subject.setAulaMiercoles(subjectCsv.aulaMiercoles);
        subject.setAulaJueves(subjectCsv.aulaJueves);
        subject.setAulaViernes(subjectCsv.aulaViernes);
        subject.setFechasSabadoNoche(subjectCsv.fechasSabadoNoche);

        // Mapeo de horarios de examenes
        subject.setParcial1Fecha(convertStringToDate(subjectCsv.parcial1Fecha));
        subject.setParcial1Hora(cleanTime(subjectCsv.parcial1Hora));
        subject.setParcial1Aula(subjectCsv.parcial1Aula);

        subject.setParcial2Fecha(convertStringToDate(subjectCsv.parcial2Fecha));
        subject.setParcial2Hora(cleanTime(subjectCsv.parcial2Hora));
        subject.setParcial2Aula(subjectCsv.parcial2Aula);

        subject.setFinal1Fecha(convertStringToDate(subjectCsv.final1Fecha));
        subject.setFinal1Hora(cleanTime(subjectCsv.final1Hora));
        subject.setFinal1Aula(subjectCsv.final1Aula);

        subject.setFinal1RevFecha(convertStringToDate(subjectCsv.final1RevFecha));
        subject.setFinal1RevHora(cleanTime(subjectCsv.final1RevHora));

        subject.setFinal2Fecha(convertStringToDate(subjectCsv.final2Fecha));
        subject.setFinal2Hora(cleanTime(subjectCsv.final2Hora));
        subject.setFinal2Aula(subjectCsv.final2Aula);

        subject.setFinal2RevFecha(convertStringToDate(subjectCsv.final2RevFecha));
        subject.setFinal2RevHora(cleanTime(subjectCsv.final2RevHora));

        subject.setComitePresidente(subjectCsv.comitePresidente);
        subject.setComiteMiembro1(subjectCsv.comiteMiembro1);
        subject.setComiteMiembro2(subjectCsv.comiteMiembro2);

        return subject;
    }

    private static Integer convertStringToNumber(String str) {
        if (str == null) {
            return 0;
        }

        String cleanedStr = str.replaceAll("[^0-9]", "");
        if (str.isBlank() || str.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(cleanedStr);
        } catch (Exception e) {
            return 0;
        }
    }

    // DATE cleaner pattern
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "(?i)^(lun|mar|mi[ée]|jue|vie|s[áa]b|dom)?\\s*(\\d{1,2})[/-](\\d{1,2})[/-](\\d{2})$");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    private static LocalDate convertStringToDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Clean bad patterns
        Matcher matcher = DATE_PATTERN.matcher(value.trim());

        if (matcher.matches()) {
            String day = String.format("%02d", Integer.parseInt(matcher.group(2)));
            String month = String.format("%02d", Integer.parseInt(matcher.group(3)));
            String year = matcher.group(4);

            String formatted = day + "/" + month + "/" + year;

            try {
                return LocalDate.parse(formatted, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        return null;
    }

    private static String cleanTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return "";
        }

        // Eliminar cualquier texto no numérico (como "hs", "h", etc.)
        String cleaned = timeStr.replaceAll("[^0-9:]", "").trim();

        // Normalizar separadores (por si usan otro carácter en lugar de :)
        cleaned = cleaned.replaceAll("[^0-9]+", ":");

        // Eliminar : duplicados y : al inicio/final
        cleaned = cleaned.replaceAll("^:|:$", "").replaceAll("::+", ":");

        // Dividir en componentes
        String[] segments = cleaned.split(":");

        try {
            // Obtener horas y minutos (ignorar segundos si existen)
            int hours = segments.length > 0 ? Integer.parseInt(segments[0]) : 0;
            int minutes = segments.length > 1 ? Integer.parseInt(segments[1]) : 0;

            // Validar rangos
            hours = Math.max(0, Math.min(hours, 23));
            minutes = Math.max(0, Math.min(minutes, 59));

            // Formatear a dos dígitos cada componente
            return String.format("%02d:%02d", hours, minutes);
        } catch (NumberFormatException e) {
            // TODO: mostrar un log de warning
            return "";
        }
    }
}
