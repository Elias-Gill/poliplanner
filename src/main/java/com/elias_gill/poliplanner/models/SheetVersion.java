package com.elias_gill.poliplanner.models;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "sheet_version")
public class SheetVersion {
    @Id
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "url")
    private String url;

    @Column(name = "parsedAt")
    @CreationTimestamp
    private LocalDate parsedAt;

    public SheetVersion(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDate getParsedAt() {
        return parsedAt;
    }
}
