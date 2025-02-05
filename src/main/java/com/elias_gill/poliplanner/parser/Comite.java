package com.elias_gill.poliplanner.parser;

import com.opencsv.bean.CsvBindByPosition;

// Clase interna para comité
public class Comite {
    @CsvBindByPosition(position = 31)
    public String presidente;

    @CsvBindByPosition(position = 32)
    public String miembro1;

    @CsvBindByPosition(position = 33)
    public String miembro2;

    @CsvBindByPosition(position = 34)
    public String aulaReunion;
}
