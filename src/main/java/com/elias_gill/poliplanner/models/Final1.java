package com.elias_gill.poliplanner.models;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.util.Date;

@Embeddable
public class Final1 {
    @Column(name = "final1_date")
    @CsvBindByPosition(position = 21)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    public Date fecha;

    @Column(name = "final1_schedule")
    @CsvBindByPosition(position = 22)
    public String hora;

    @Column(name = "final1_classroom")
    @CsvBindByPosition(position = 23)
    public String aula;

    @Column(name = "final1_review_date")
    @CsvBindByPosition(position = 24)
    @CsvDate("dd/MM/yyyy")
    @PreAssignmentProcessor(processor = CustomDateSanitizer_.class)
    public Date fechaRevisión;

    @Column(name = "final1_review_schedule")
    @CsvBindByPosition(position = 25)
    public String horaRevisión;
}
