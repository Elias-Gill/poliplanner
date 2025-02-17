package com.elias_gill.poliplanner.models;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

public class Final2 {
    @CsvBindByPosition(position = 26)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    public Date fecha;

    @CsvBindByPosition(position = 27)
    public String hora;

    @CsvBindByPosition(position = 28)
    public String aula;

    @CsvBindByPosition(position = 29)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    public Date fechaRevisión;

    @CsvBindByPosition(position = 30)
    public String horaRevisión;
}
