package poliplanner.excel.parser;

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
        subject.setSemestre(subjectCsv.semestre);

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
        subject.setParcial1Fecha(subjectCsv.parcial1Fecha);
        subject.setParcial1Hora(subjectCsv.parcial1Hora);
        subject.setParcial1Aula(subjectCsv.parcial1Aula);

        subject.setParcial2Fecha(subjectCsv.parcial2Fecha);
        subject.setParcial2Hora(subjectCsv.parcial2Hora);
        subject.setParcial2Aula(subjectCsv.parcial2Aula);

        subject.setFinal1Fecha(subjectCsv.final1Fecha);
        subject.setFinal1Hora(subjectCsv.final1Hora);
        subject.setFinal1Aula(subjectCsv.final1Aula);

        subject.setFinal1RevFecha(subjectCsv.final1RevFecha);
        subject.setFinal1RevHora(subjectCsv.final1RevHora);

        subject.setFinal2Fecha(subjectCsv.final2Fecha);
        subject.setFinal2Hora(subjectCsv.final2Hora);
        subject.setFinal2Aula(subjectCsv.final2Aula);

        subject.setFinal2RevFecha(subjectCsv.final2RevFecha);
        subject.setFinal2RevHora(subjectCsv.final2RevHora);

        subject.setComitePresidente(subjectCsv.comitePresidente);
        subject.setComiteMiembro1(subjectCsv.comiteMiembro1);
        subject.setComiteMiembro2(subjectCsv.comiteMiembro2);

        return subject;
    }
}
