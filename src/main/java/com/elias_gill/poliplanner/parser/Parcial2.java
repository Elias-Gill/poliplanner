package com.elias_gill.poliplanner.parser;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

public class Parcial2 {
    @CsvBindByPosition(position = 18)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer.class)
    public Date fecha;

    @CsvBindByPosition(position = 19)
    public String hora;

    @CsvBindByPosition(position = 20)
    public String aula;
}
