package com.elias_gill.poliplanner.parser;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvRecurse;

public class Subject {
    @CsvBindByPosition(position = 0)
    public String item;

    @CsvBindByPosition(position = 1)
    public String departamento;

    @CsvBindByPosition(position = 2)
    public String asignatura;

    @CsvBindByPosition(position = 3)
    public String nivel;

    @CsvBindByPosition(position = 4)
    public String semestre;

    @CsvBindByPosition(position = 5)
    public String codigoCarrera;

    @CsvBindByPosition(position = 6)
    public String enfasis;

    @CsvBindByPosition(position = 7)
    public String plan;

    @CsvBindByPosition(position = 8)
    public String turno;

    @CsvBindByPosition(position = 9)
    public String seccion;

    @CsvBindByPosition(position = 10)
    public String plataformaVirtual;

    @CsvBindByPosition(position = 11)
    public String titulo;

    @CsvBindByPosition(position = 12)
    public String apellido;

    @CsvBindByPosition(position = 13)
    public String nombre;

    @CsvBindByPosition(position = 14)
    public String emailInstitucional;

    @CsvRecurse
    public HorarioSemana horario;

    @CsvRecurse
    public Exams exams;

    @Override
    public String toString() {
        return String.format("Asignatura: %s\n" +
                "Departamento: %s\n" +
                "Nivel: %s\n" +
                "Semestre/Grupo: %s\n" +
                "Código Carrera: %s\n" +
                "Enfasis: %s\n" +
                "Plan: %s\n" +
                "Turno: %s\n" +
                "Sección: %s\n" +
                "Plataforma Virtual: %s\n" +
                "Profesor: %s, %s\n" +
                "Email Institucional: %s\n" +
                "Exámenes Parciales:\n" +
                "%s\n" +
                "%s\n" +
                "Exámenes Finales:\n" +
                "%s\n" +
                "%s\n" +
                "%s\n\n",
                asignatura, departamento, nivel, semestre,
                codigoCarrera, enfasis, plan, turno, seccion,
                plataformaVirtual, apellido, nombre, emailInstitucional,
                exams.parcial1, exams.parcial2,
                exams.final1, exams.final2,
                horario);
    }
}
