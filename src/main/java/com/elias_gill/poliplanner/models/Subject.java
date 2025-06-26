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

@Entity
@Table(name = "subjects")
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
    private String semestre;

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

    @Embedded private ExamsCompact exams;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career carrera) {
        this.career = carrera;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getTituloProfesor() {
        return tituloProfesor;
    }

    public void setTituloProfesor(String tituloProfesor) {
        this.tituloProfesor = tituloProfesor;
    }

    public String getApellidoProfesor() {
        return apellidoProfesor;
    }

    public void setApellidoProfesor(String apellidoProfesor) {
        this.apellidoProfesor = apellidoProfesor;
    }

    public String getNombreProfesor() {
        return nombreProfesor;
    }

    public void setNombreProfesor(String nombreProfesor) {
        this.nombreProfesor = nombreProfesor;
    }

    public String getEmailProfesor() {
        return emailProfesor;
    }

    public void setEmailProfesor(String emailProfesor) {
        this.emailProfesor = emailProfesor;
    }

    public ExamsCompact getExams() {
        return exams;
    }

    public void setExams(ExamsCompact exams) {
        this.exams = exams;
    }

    public String getLunes() {
        return lunes;
    }

    public void setLunes(String lunes) {
        this.lunes = lunes;
    }

    public String getAulaLunes() {
        return aulaLunes;
    }

    public void setAulaLunes(String aulaLunes) {
        this.aulaLunes = aulaLunes;
    }

    public String getMartes() {
        return martes;
    }

    public void setMartes(String martes) {
        this.martes = martes;
    }

    public String getAulaMartes() {
        return aulaMartes;
    }

    public void setAulaMartes(String aulaMartes) {
        this.aulaMartes = aulaMartes;
    }

    public String getMiercoles() {
        return miercoles;
    }

    public void setMiercoles(String miercoles) {
        this.miercoles = miercoles;
    }

    public String getAulaMiercoles() {
        return aulaMiercoles;
    }

    public void setAulaMiercoles(String aulaMiercoles) {
        this.aulaMiercoles = aulaMiercoles;
    }

    public String getJueves() {
        return jueves;
    }

    public void setJueves(String jueves) {
        this.jueves = jueves;
    }

    public String getAulaJueves() {
        return aulaJueves;
    }

    public void setAulaJueves(String aulaJueves) {
        this.aulaJueves = aulaJueves;
    }

    public String getViernes() {
        return viernes;
    }

    public void setViernes(String viernes) {
        this.viernes = viernes;
    }

    public String getAulaViernes() {
        return aulaViernes;
    }

    public void setAulaViernes(String aulaViernes) {
        this.aulaViernes = aulaViernes;
    }

    public String getSabado() {
        return sabado;
    }

    public void setSabado(String sabado) {
        this.sabado = sabado;
    }

    public String getFechasSabadoNoche() {
        return fechasSabadoNoche;
    }

    public void setFechasSabadoNoche(String fechasSabadoNoche) {
        this.fechasSabadoNoche = fechasSabadoNoche;
    }
}
