package com.elias_gill.poliplanner.models;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.util.Date;

@Embeddable
public class Parcial1 {
    @Column(name = "partial1_date")
    @CsvBindByPosition(position = 15)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    public Date fecha;

    @Column(name = "partial1_schedule")
    @CsvBindByPosition(position = 16)
    public String hora;

    @Column(name = "partial1_classroom")
    @CsvBindByPosition(position = 17)
    public String aula;
}
