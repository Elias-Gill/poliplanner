package com.elias_gill.poliplanner.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LatestExcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "latest_update")
    private LocalDate latestUpdate;

    public LatestExcel(String fileName) {
        this.fileName = fileName;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return fileName;
    }
}
