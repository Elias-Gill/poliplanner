package com.elias_gill.poliplanner.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Career {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "career_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_version")
    private SheetVersion version;

    public Career() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public SheetVersion getVersion() {
        return version;
    }

    public void setVersion(SheetVersion version) {
        this.version = version;
    }

    public Career(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
