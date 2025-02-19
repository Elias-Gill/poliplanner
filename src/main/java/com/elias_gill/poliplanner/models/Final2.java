package com.elias_gill.poliplanner.models;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.util.Date;

@Embeddable
public class Final2 {
    @Column(name = "final2_date")
    @CsvBindByPosition(position = 26)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    public Date fecha;

    @Column(name = "final2_schedule")
    @CsvBindByPosition(position = 27)
    public String hora;

    @Column(name = "final2_classroom")
    @CsvBindByPosition(position = 28)
    public String aula;

    @Column(name = "final2_review_date")
    @CsvBindByPosition(position = 29)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    public Date fechaRevisión;

    @Column(name = "final2_review_schedule")
    @CsvBindByPosition(position = 30)
    public String horaRevisión;
}
