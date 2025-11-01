package poliplanner.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "subjects", indexes = @Index(columnList = "subject_name"))
@Getter
@Setter
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    public Subject(String name, String seccion, Career career) {
        this.career = career;
        this.nombreAsignatura = name;
        this.seccion = seccion;
    }

    // ######################
    // #### INFO GENERAL ####
    // ######################

    @ManyToOne
    @JoinColumn(name = "subject_career")
    private Career career;

    @Column(name = "subject_department")
    private String departamento;

    @Column(name = "subject_name")
    private String nombreAsignatura;

    @Column(name = "subject_semester")
    private Integer semestre;

    @Column(name = "subject_section")
    private String seccion;

    @Column(name = "subject_teacher_title")
    private String tituloProfesor;

    @Column(name = "subject_teacher_lastname")
    private String apellidoProfesor;

    @Column(name = "subject_teacher_name")
    private String nombreProfesor;

    @Column(name = "subject_teacher_mail")
    private String emailProfesor;

    // ##############################
    // #### HORARIO DE LA SEMANA ####
    // ##############################

    @Column(name = "lunes")
    private String lunes;

    @Column(name = "aula_lunes")
    private String aulaLunes;

    @Column(name = "martes")
    private String martes;

    @Column(name = "aula_martes")
    private String aulaMartes;

    @Column(name = "miercoles")
    private String miercoles;

    @Column(name = "aula_miercoles")
    private String aulaMiercoles;

    @Column(name = "jueves")
    private String jueves;

    @Column(name = "aula_jueves")
    private String aulaJueves;

    @Column(name = "viernes")
    private String viernes;

    @Column(name = "aula_viernes")
    private String aulaViernes;

    @Column(name = "sabado")
    private String sabado;

    @Column(name = "fechas_sabado_noche")
    private String fechasSabadoNoche;

    // #############################
    // ######## EXAMENES ###########
    // #############################

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
