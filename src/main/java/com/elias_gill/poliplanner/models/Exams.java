package com.elias_gill.poliplanner.models;

import com.opencsv.bean.CsvRecurse;

public class Exams {
    @CsvRecurse
    public Parcial1 parcial1;

    @CsvRecurse
    public Parcial2 parcial2;

    @CsvRecurse
    public Final1 final1;

    @CsvRecurse
    public Final2 final2;

    @CsvRecurse
    public Comite comite;
}
