package com.elias_gill.poliplanner.excel.parser;

public class SubjectCsvDTO {
    // Info general
    public String departamento;
    public String nombreAsignatura;
    public String nivel; // Cantidad de correlativas
    public String semestre;
    public String seccion;

    // Info del docente
    public String tituloProfesor;
    public String apellidoProfesor;
    public String nombreProfesor;
    public String emailProfesor;

    // -- Exámenes --
    // Primer parcial
    public String parcial1Fecha;
    public String parcial1Hora;
    public String parcial1Aula;

    // Segundo parcial
    public String parcial2Fecha;
    public String parcial2Hora;
    public String parcial2Aula;

    // Primer Final
    public String final1Fecha;
    public String final1Hora;
    public String final1Aula;
    public String final1RevFecha;
    public String final1RevHora;

    // Segundo Final
    public String final2Fecha;
    public String final2Hora;
    public String final2Aula;
    public String final2RevFecha;
    public String final2RevHora;

    // Cosas de Revision
    public String comitePresidente;
    public String comiteMiembro1;
    public String comiteMiembro2;

    // Horario semanal
    public String aulaLunes;
    public String lunes;

    public String aulaMartes;
    public String martes;

    public String aulaMiercoles;
    public String miercoles;

    public String aulaJueves;
    public String jueves;

    public String aulaViernes;
    public String viernes;

    public String aulaSabado;
    public String sabado;
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
