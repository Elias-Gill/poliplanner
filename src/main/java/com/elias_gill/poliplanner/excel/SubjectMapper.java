package com.elias_gill.poliplanner.excel;

import com.elias_gill.poliplanner.excel.dto.SubjectCsv;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.models.ExamsCompact;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public static Subject mapToSubject(SubjectCsv subjectCsv) {
        if (subjectCsv == null) {
            return null;
        }

        // Crear objeto Subject
        Subject subject = new Subject();
        subject.setDepartamento(subjectCsv.departamento);
        subject.setNombreAsignatura(subjectCsv.nombreAsignatura);
        subject.setSemestre(subjectCsv.semestre);
        subject.setSeccion(subjectCsv.seccion);
        subject.setTituloProfesor(subjectCsv.tituloProfesor);
        subject.setApellidoProfesor(subjectCsv.apellidoProfesor);
        subject.setNombreProfesor(subjectCsv.nombreProfesor);
        subject.setEmailProfesor(subjectCsv.emailProfesor);
        subject.setLunes(subjectCsv.lunes);
        subject.setAulaLunes(subjectCsv.aulaLunes);
        subject.setMartes(subjectCsv.martes);
        subject.setAulaMartes(subjectCsv.aulaMartes);
        subject.setMiercoles(subjectCsv.miercoles);
        subject.setAulaMiercoles(subjectCsv.aulaMiercoles);
        subject.setJueves(subjectCsv.jueves);
        subject.setAulaJueves(subjectCsv.aulaJueves);
        subject.setViernes(subjectCsv.viernes);
        subject.setAulaViernes(subjectCsv.aulaViernes);
        subject.setSabado(subjectCsv.sabado);
        subject.setFechasSabadoNoche(subjectCsv.fechasSabadoNoche);

        // Crear el objeto ExamsCompact (embebido en Subject)
        ExamsCompact exams = new ExamsCompact();

        // Mapear los exámenes
        exams.setParcial1Fecha(subjectCsv.parcial1Fecha);
        exams.setParcial1Hora(subjectCsv.parcial1Hora);
        exams.setParcial1Aula(subjectCsv.parcial1Aula);

        exams.setParcial2Fecha(subjectCsv.parcial2Fecha);
        exams.setParcial2Hora(subjectCsv.parcial2Hora);
        exams.setParcial2Aula(subjectCsv.parcial2Aula);

        exams.setFinal1Fecha(subjectCsv.final1Fecha);
        exams.setFinal1Hora(subjectCsv.final1Hora);
        exams.setFinal1Aula(subjectCsv.final1Aula);

        exams.setFinal1RevFecha(subjectCsv.final1RevFecha);
        exams.setFinal1RevHora(subjectCsv.final1RevHora);

        exams.setFinal2Fecha(subjectCsv.final2Fecha);
        exams.setFinal2Hora(subjectCsv.final2Hora);
        exams.setFinal2Aula(subjectCsv.final2Aula);

        exams.setFinal2RevFecha(subjectCsv.final2RevFecha);
        exams.setFinal2RevHora(subjectCsv.final2RevHora);

        exams.setComitePresidente(subjectCsv.comitePresidente);
        exams.setComiteMiembro1(subjectCsv.comiteMiembro1);
        exams.setComiteMiembro2(subjectCsv.comiteMiembro2);

        // Asignar el objeto ExamsCompact al Subject
        subject.setExams(exams);

        return subject;
    }
}
