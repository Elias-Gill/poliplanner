package com.elias_gill.poliplanner.models;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvRecurse;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;

@Entity
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "subject_item")
    //@CsvBindByPosition(position = 0)
    //public String item;

    @Column(name = "subject_department")
    @CsvBindByPosition(position = 1)
    public String departamento;

    @Column(name = "subject_name")
    @CsvBindByPosition(position = 2)
    public String nombreAsignatura;

    //@Column(name = "")
    //@CsvBindByPosition(position = 3)
    //public String nivel;

    @Column(name = "subject_semester")
    @CsvBindByPosition(position = 4)
    public String semestre;

    //@Column(name = "")
    //@CsvBindByPosition(position = 5)
    //public String codigoCarrera;

    //@Column(name = "")
    //@CsvBindByPosition(position = 6)
    //public String enfasis;

    //@Column(name = "")
    //@CsvBindByPosition(position = 7)
    //public String anho_malla;

    //@Column(name = "") // nota, la seccion ya viene con turno
    //@CsvBindByPosition(position = 8)
    //public String turno;

    @Column(name = "subject_section")
    @CsvBindByPosition(position = 9)
    public String seccion;

    //@Column(name = "")
    //@CsvBindByPosition(position = 10)
    //public String plataformaVirtual;

    @Column(name = "subject_teacher_title")
    @CsvBindByPosition(position = 11)
    public String tituloProfesor;

    @Column(name = "subject_teacher_lastname")
    @CsvBindByPosition(position = 12)
    public String apellidoProfesor;

    @Column(name = "subject_teacher_name")
    @CsvBindByPosition(position = 13)
    public String nombreProfesor;

    @Column(name = "subject_teacher_mail")
    @CsvBindByPosition(position = 13)
    @CsvBindByPosition(position = 14)
    public String emailProfesor;

    @Embedded
    @CsvRecurse
    public HorarioSemana horario;

    @Embedded
    @CsvRecurse
    public Exams exams;

    @Override
    public String toString() {
        return String.format("Asignatura: %s\n" +
                "Departamento: %s\n" +
                "Semestre: %s\n" +
                "Sección: %s\n" +
                "Profesor: %s, %s\n" +
                "Email Institucional: %s\n" +
                "Exámenes Parciales:\n" +
                "%s\n" +
                "%s\n" +
                "Exámenes Finales:\n" +
                "%s\n" +
                "%s\n" +
                "%s\n\n",
                nombreAsignatura, departamento, semestre, seccion, 
                apellidoProfesor, nombreProfesor, emailProfesor,
                exams.parcial1, exams.parcial2,
                exams.final1, exams.final2,
                horario);
    }
}
