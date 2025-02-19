package com.elias_gill.poliplanner.models;

import com.opencsv.bean.CsvBindByPosition;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

@Embeddable
public class Comite {
    @Column(name = "committee_president")
    @CsvBindByPosition(position = 31)
    public String presidente;

    @Column(name = "committee_member1")
    @CsvBindByPosition(position = 32)
    public String miembro1;

    @Column(name = "committee_member2")
    @CsvBindByPosition(position = 33)
    public String miembro2;

    @Column(name = "committee_meeting_room")
    @CsvBindByPosition(position = 34)
    public String aulaReunion;
}
