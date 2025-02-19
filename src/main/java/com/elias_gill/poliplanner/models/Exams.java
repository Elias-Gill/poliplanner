package com.elias_gill.poliplanner.models;

import com.opencsv.bean.CsvRecurse;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class Exams {
    @Embedded
    @CsvRecurse
    public Parcial1 parcial1;

    @Embedded
    @CsvRecurse
    public Parcial2 parcial2;

    @Embedded
    @CsvRecurse
    public Final1 final1;

    @Embedded
    @CsvRecurse
    public Final2 final2;

    @Embedded
    @CsvRecurse
    public Comite comite;
}
