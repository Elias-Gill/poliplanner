package com.elias_gill.poliplanner.models.metadata;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "subjects_prerrequisite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectsPrerrequisites {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class1_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SubjectsMetadata class1;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class2_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SubjectsMetadata class2;
}
