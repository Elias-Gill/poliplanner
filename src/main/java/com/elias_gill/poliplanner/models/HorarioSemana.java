package com.elias_gill.poliplanner.models;

import com.opencsv.bean.CsvBindByPosition;

public class HorarioSemana {
    @CsvBindByPosition(position = 35)
    public String lunes;
    @CsvBindByPosition(position = 36)
    public String aulaLunes;

    @CsvBindByPosition(position = 37)
    public String martes;
    @CsvBindByPosition(position = 38)
    public String aulaMartes;

    @CsvBindByPosition(position = 39)
    public String miercoles;
    @CsvBindByPosition(position = 40)
    public String aulaMiercoles;

    @CsvBindByPosition(position = 41)
    public String jueves;
    @CsvBindByPosition(position = 42)
    public String aulaJueves;

    @CsvBindByPosition(position = 43)
    public String viernes;
    @CsvBindByPosition(position = 44)
    public String aulaViernes;

    @CsvBindByPosition(position = 45)
    public String sabado;
    @CsvBindByPosition(position = 46)
    public String fechasSabadoNoche;

    @Override
    public String toString() {
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
            sb.append("Sábado: ").append(sabado);
            sb.append("\n");
        }

        if (fechasSabadoNoche != null && !fechasSabadoNoche.isEmpty()) {
            sb.append("Fechas Sábado Noche: ").append(fechasSabadoNoche).append("\n");
        }

        return sb.toString().trim();
    }
}
