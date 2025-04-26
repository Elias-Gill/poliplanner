package com.elias_gill.poliplanner.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "careers")
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private long id;

    @Column(name = "career_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_version_id")
    private SheetVersion version;

    // --- Constructores ---
    public Career() {
    }

    public Career(String name) {
        this.name = name;
    }

    // --- Getters y Setters ---
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
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
}
