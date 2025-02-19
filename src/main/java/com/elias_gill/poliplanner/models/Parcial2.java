package com.elias_gill.poliplanner.models;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.util.Date;

@Embeddable
public class Parcial2 {
    @CsvBindByPosition(position = 18)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    @Column(name = "partial2_date")
    public Date fecha;

    @Column(name = "partial2_schedule")
    @CsvBindByPosition(position = 19)
    public String hora;

    @Column(name = "partial2_classroom")
    @CsvBindByPosition(position = 20)
    public String aula;
}
