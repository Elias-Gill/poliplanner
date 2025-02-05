package com.elias_gill.poliplanner.parser;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

public class Final1 {
    @CsvBindByPosition(position = 21)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer.class)
    public Date fecha;

    @CsvBindByPosition(position = 22)
    public String hora;

    @CsvBindByPosition(position = 23)
    public String aula;

    @CsvBindByPosition(position = 24)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer.class)
    public Date fechaRevisión;

    @CsvBindByPosition(position = 25)
    public String horaRevisión;
}
