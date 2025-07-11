package com.elias_gill.poliplanner.models;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sheet_version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SheetVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "parsed_at", nullable = false)
    @CreationTimestamp
    private Date parsedAt;

    public SheetVersion(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }
}
