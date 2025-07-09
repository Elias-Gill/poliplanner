package com.elias_gill.poliplanner.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ExamsCompact {
    @Column(name = "parcial1_fecha")
    private LocalDate parcial1Fecha;

    @Column(name = "parcial1_hora")
    private String parcial1Hora;

    @Column(name = "parcial1_aula")
    private String parcial1Aula;

    @Column(name = "parcial2_fecha")
    private LocalDate parcial2Fecha;

    @Column(name = "parcial2_hora")
    private String parcial2Hora;

    @Column(name = "parcial2_aula")
    private String parcial2Aula;

    @Column(name = "final1_fecha")
    private LocalDate final1Fecha;

    @Column(name = "final1_hora")
    private String final1Hora;

    @Column(name = "final1_aula")
    private String final1Aula;

    @Column(name = "final1_rev_fecha")
    private LocalDate final1RevFecha;

    @Column(name = "final1_rev_hora")
    private String final1RevHora;

    @Column(name = "final2_fecha")
    private LocalDate final2Fecha;

    @Column(name = "final2_hora")
    private String final2Hora;

    @Column(name = "final2_aula")
    private String final2Aula;

    @Column(name = "final2_rev_fecha")
    private LocalDate final2RevFecha;

    @Column(name = "final2_rev_hora")
    private String final2RevHora;

    @Column(name = "comite_presidente")
    private String comitePresidente;

    @Column(name = "comite_miembro1")
    private String comiteMiembro1;

    @Column(name = "comite_miembro2")
    private String comiteMiembro2;
}
