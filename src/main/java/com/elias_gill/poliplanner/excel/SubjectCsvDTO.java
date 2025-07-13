package com.elias_gill.poliplanner.excel;

import com.opencsv.bean.CsvBindByPosition;

public class SubjectCsvDTO {
    @CsvBindByPosition(position = 1)
    public String departamento;

    @CsvBindByPosition(position = 2)
    public String nombreAsignatura;

    // NOTE: algunas materias tienen semestre y nivel, otras solo semestre, y otras
    // solo nivel la verdad es que no tengo ni idea de que significa esa
    // nomenclatura para los de la facultad.
    @CsvBindByPosition(position = 3)
    public String nivel;

    @CsvBindByPosition(position = 4)
    public String semestre;

    @CsvBindByPosition(position = 9)
    public String seccion;

    @CsvBindByPosition(position = 11)
    public String tituloProfesor;

    @CsvBindByPosition(position = 12)
    public String apellidoProfesor;

    @CsvBindByPosition(position = 13)
    public String nombreProfesor;

    @CsvBindByPosition(position = 14)
    public String emailProfesor;

    // Exámenes compactados
    @CsvBindByPosition(position = 15)
    public String parcial1Fecha;

    @CsvBindByPosition(position = 16)
    public String parcial1Hora;

    @CsvBindByPosition(position = 17)
    public String parcial1Aula;

    @CsvBindByPosition(position = 18)
    public String parcial2Fecha;

    @CsvBindByPosition(position = 19)
    public String parcial2Hora;

    @CsvBindByPosition(position = 20)
    public String parcial2Aula;

    @CsvBindByPosition(position = 21)
    public String final1Fecha;

    @CsvBindByPosition(position = 22)
    public String final1Hora;

    @CsvBindByPosition(position = 23)
    public String final1Aula;

    @CsvBindByPosition(position = 24)
    public String final1RevFecha;

    @CsvBindByPosition(position = 25)
    public String final1RevHora;

    @CsvBindByPosition(position = 26)
    public String final2Fecha;

    @CsvBindByPosition(position = 27)
    public String final2Hora;

    @CsvBindByPosition(position = 28)
    public String final2Aula;

    @CsvBindByPosition(position = 29)
    public String final2RevFecha;

    @CsvBindByPosition(position = 30)
    public String final2RevHora;

    @CsvBindByPosition(position = 31)
    public String comitePresidente;

    @CsvBindByPosition(position = 32)
    public String comiteMiembro1;

    @CsvBindByPosition(position = 33)
    public String comiteMiembro2;

    @CsvBindByPosition(position = 34)
    public String aulaLunes;

    @CsvBindByPosition(position = 35)
    public String lunes;

    @CsvBindByPosition(position = 36)
    public String aulaMartes;

    @CsvBindByPosition(position = 37)
    public String martes;

    @CsvBindByPosition(position = 38)
    public String aulaMiercoles;

    @CsvBindByPosition(position = 39)
    public String miercoles;

    @CsvBindByPosition(position = 40)
    public String aulaJueves;

    @CsvBindByPosition(position = 41)
    public String jueves;

    @CsvBindByPosition(position = 42)
    public String aulaViernes;

    @CsvBindByPosition(position = 43)
    public String viernes;

    @CsvBindByPosition(position = 44)
    public String aulaSabado;

    @CsvBindByPosition(position = 45)
    public String sabado;

    @CsvBindByPosition(position = 46)
    public String fechasSabadoNoche;

    // Método auxiliar opcional (no se persiste, solo para depurar o mostrar)
    public String semanaToString() {
        StringBuilder sb = new StringBuilder("Horario:\n");

        if (lunes != null && !lunes.isEmpty()) {
            sb.append("Lunes: ").append(lunes);
            if (aulaLunes != null && !aulaLunes.isEmpty()) {
                sb.append(" (Aula: ").append(aulaLunes).append(")");
            }
            sb.append("\n");
        }

        if (martes != null && !martes.isEmpty()) {
            sb.append("Martes: ").append(martes);
            if (aulaMartes != null && !aulaMartes.isEmpty()) {
                sb.append(" (Aula: ").append(aulaMartes).append(")");
            }
            sb.append("\n");
        }

        if (miercoles != null && !miercoles.isEmpty()) {
            sb.append("Miércoles: ").append(miercoles);
            if (aulaMiercoles != null && !aulaMiercoles.isEmpty()) {
                sb.append(" (Aula: ").append(aulaMiercoles).append(")");
            }
            sb.append("\n");
        }

        if (jueves != null && !jueves.isEmpty()) {
            sb.append("Jueves: ").append(jueves);
            if (aulaJueves != null && !aulaJueves.isEmpty()) {
                sb.append(" (Aula: ").append(aulaJueves).append(")");
            }
            sb.append("\n");
        }

        if (viernes != null && !viernes.isEmpty()) {
            sb.append("Viernes: ").append(viernes);
            if (aulaViernes != null && !aulaViernes.isEmpty()) {
                sb.append(" (Aula: ").append(aulaViernes).append(")");
            }
            sb.append("\n");
        }

        if (sabado != null && !sabado.isEmpty()) {
            sb.append("Sábado: ").append(sabado).append("\n");
        }

        if (fechasSabadoNoche != null && !fechasSabadoNoche.isEmpty()) {
            sb.append("Fechas Sábado Noche: ").append(fechasSabadoNoche).append("\n");
        }

        return sb.toString().trim();
    }
}
