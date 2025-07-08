package com.elias_gill.poliplanner.excel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.elias_gill.poliplanner.excel.dto.SubjectCsv;
import com.elias_gill.poliplanner.models.ExamsCompact;
import com.elias_gill.poliplanner.models.Subject;

@Component
public class SubjectMapper {
    public static Subject mapToSubject(SubjectCsv subjectCsv) {
        if (subjectCsv == null) {
            return null;
        }

        Subject subject = new Subject();
        // informacion general de la materia
        subject.setDepartamento(subjectCsv.departamento);
        subject.setNombreAsignatura(subjectCsv.nombreAsignatura);
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
        ExamsCompact exams = new ExamsCompact();

        exams.setParcial1Fecha(convertStringToDate(subjectCsv.parcial1Fecha));
        exams.setParcial1Hora((subjectCsv.parcial1Hora));
        exams.setParcial1Aula(subjectCsv.parcial1Aula);

        exams.setParcial2Fecha(convertStringToDate(subjectCsv.parcial2Fecha));
        exams.setParcial2Hora(subjectCsv.parcial2Hora);
        exams.setParcial2Aula(subjectCsv.parcial2Aula);

        exams.setFinal1Fecha(convertStringToDate(subjectCsv.final1Fecha));
        exams.setFinal1Hora(subjectCsv.final1Hora);
        exams.setFinal1Aula(subjectCsv.final1Aula);

        exams.setFinal1RevFecha(convertStringToDate(subjectCsv.final1RevFecha));
        exams.setFinal1RevHora(subjectCsv.final1RevHora);

        exams.setFinal2Fecha(convertStringToDate(subjectCsv.final2Fecha));
        exams.setFinal2Hora(subjectCsv.final2Hora);
        exams.setFinal2Aula(subjectCsv.final2Aula);

        exams.setFinal2RevFecha(convertStringToDate(subjectCsv.final2RevFecha));
        exams.setFinal2RevHora(subjectCsv.final2RevHora);

        exams.setComitePresidente(subjectCsv.comitePresidente);
        exams.setComiteMiembro1(subjectCsv.comiteMiembro1);
        exams.setComiteMiembro2(subjectCsv.comiteMiembro2);

        subject.setExams(exams);

        return subject;
    }

    private static Integer convertStringToNumber(String str) {
        str = str.trim();
        if (str == null || str.isBlank() || str.isEmpty()) {
            return 0;
        }

        String cleanedStr = str.replaceAll("[^0-9]", "");
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

    public static LocalDate convertStringToDate(String value) {
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
}
