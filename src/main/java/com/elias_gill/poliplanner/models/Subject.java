package com.elias_gill.poliplanner.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_career")
    private Career career;

    @Column(name = "subject_department")
    private String departamento;

    @Column(name = "subject_name")
    private String nombreAsignatura;

    @Column(name = "subject_semester")
    private Integer semestre;

    @Column(name = "subject_section")
    private String seccion;

    @Column(name = "subject_teacher_title")
    private String tituloProfesor;

    @Column(name = "subject_teacher_lastname")
    private String apellidoProfesor;

    @Column(name = "subject_teacher_name")
    private String nombreProfesor;

    @Column(name = "subject_teacher_mail")
    private String emailProfesor;

    @Embedded
    private ExamsCompact exams;

    @Column(name = "lunes")
    private String lunes;

    @Column(name = "aula_lunes")
    private String aulaLunes;

    @Column(name = "martes")
    private String martes;

    @Column(name = "aula_martes")
    private String aulaMartes;

    @Column(name = "miercoles")
    private String miercoles;

    @Column(name = "aula_miercoles")
    private String aulaMiercoles;

    @Column(name = "jueves")
    private String jueves;

    @Column(name = "aula_jueves")
    private String aulaJueves;

    @Column(name = "viernes")
    private String viernes;

    @Column(name = "aula_viernes")
    private String aulaViernes;

    @Column(name = "sabado")
    private String sabado;

    @Column(name = "fechas_sabado_noche")
    private String fechasSabadoNoche;
}
