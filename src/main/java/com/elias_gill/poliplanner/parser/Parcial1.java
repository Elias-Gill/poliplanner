package com.elias_gill.poliplanner.parser;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

// Clase interna para el primer examen parcial
public class Parcial1 {
    @CsvBindByPosition(position = 15)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer.class)
    public Date fecha;

    @CsvBindByPosition(position = 16)
    public String hora;

    @CsvBindByPosition(position = 17)
    public String aula;
}
