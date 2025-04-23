package com.elias_gill.poliplanner.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
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

    // --- Getters y setters ---

    public LocalDate getParcial1Fecha() {
        return parcial1Fecha;
    }

    public void setParcial1Fecha(LocalDate parcial1Fecha) {
        this.parcial1Fecha = parcial1Fecha;
    }

    public String getParcial1Hora() {
        return parcial1Hora;
    }

    public void setParcial1Hora(String parcial1Hora) {
        this.parcial1Hora = parcial1Hora;
    }

    public String getParcial1Aula() {
        return parcial1Aula;
    }

    public void setParcial1Aula(String parcial1Aula) {
        this.parcial1Aula = parcial1Aula;
    }

    public LocalDate getParcial2Fecha() {
        return parcial2Fecha;
    }

    public void setParcial2Fecha(LocalDate parcial2Fecha) {
        this.parcial2Fecha = parcial2Fecha;
    }

    public String getParcial2Hora() {
        return parcial2Hora;
    }

    public void setParcial2Hora(String parcial2Hora) {
        this.parcial2Hora = parcial2Hora;
    }

    public String getParcial2Aula() {
        return parcial2Aula;
    }

    public void setParcial2Aula(String parcial2Aula) {
        this.parcial2Aula = parcial2Aula;
    }

    public LocalDate getFinal1Fecha() {
        return final1Fecha;
    }

    public void setFinal1Fecha(LocalDate final1Fecha) {
        this.final1Fecha = final1Fecha;
    }

    public String getFinal1Hora() {
        return final1Hora;
    }

    public void setFinal1Hora(String final1Hora) {
        this.final1Hora = final1Hora;
    }

    public String getFinal1Aula() {
        return final1Aula;
    }

    public void setFinal1Aula(String final1Aula) {
        this.final1Aula = final1Aula;
    }

    public LocalDate getFinal1RevFecha() {
        return final1RevFecha;
    }

    public void setFinal1RevFecha(LocalDate final1RevFecha) {
        this.final1RevFecha = final1RevFecha;
    }

    public String getFinal1RevHora() {
        return final1RevHora;
    }

    public void setFinal1RevHora(String final1RevHora) {
        this.final1RevHora = final1RevHora;
    }

    public LocalDate getFinal2Fecha() {
        return final2Fecha;
    }

    public void setFinal2Fecha(LocalDate final2Fecha) {
        this.final2Fecha = final2Fecha;
    }

    public String getFinal2Hora() {
        return final2Hora;
    }

    public void setFinal2Hora(String final2Hora) {
        this.final2Hora = final2Hora;
    }

    public String getFinal2Aula() {
        return final2Aula;
    }

    public void setFinal2Aula(String final2Aula) {
        this.final2Aula = final2Aula;
    }

    public LocalDate getFinal2RevFecha() {
        return final2RevFecha;
    }

    public void setFinal2RevFecha(LocalDate final2RevFecha) {
        this.final2RevFecha = final2RevFecha;
    }

    public String getFinal2RevHora() {
        return final2RevHora;
    }

    public void setFinal2RevHora(String final2RevHora) {
        this.final2RevHora = final2RevHora;
    }

    public String getComitePresidente() {
        return comitePresidente;
    }

    public void setComitePresidente(String comitePresidente) {
        this.comitePresidente = comitePresidente;
    }

    public String getComiteMiembro1() {
        return comiteMiembro1;
    }

    public void setComiteMiembro1(String comiteMiembro1) {
        this.comiteMiembro1 = comiteMiembro1;
    }

    public String getComiteMiembro2() {
        return comiteMiembro2;
    }

    public void setComiteMiembro2(String comiteMiembro2) {
        this.comiteMiembro2 = comiteMiembro2;
    }
}
