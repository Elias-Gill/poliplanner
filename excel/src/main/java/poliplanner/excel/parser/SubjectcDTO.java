package poliplanner.excel.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubjectcDTO {
    // Info general
    public String departamento;
    public String nombreAsignatura;
    public Integer semestre; // Cantidad de correlativas
    public String seccion;

    // Info del docente
    public String tituloProfesor;
    public String apellidoProfesor;
    public String nombreProfesor;
    public String emailProfesor;

    // -- Exámenes --
    // Primer parcial
    public LocalDate parcial1Fecha;
    public String parcial1Hora;
    public String parcial1Aula;

    // Segundo parcial
    public LocalDate parcial2Fecha;
    public String parcial2Hora;
    public String parcial2Aula;

    // Primer Final
    public LocalDate final1Fecha;
    public String final1Hora;
    public String final1Aula;
    public LocalDate final1RevFecha;
    public String final1RevHora;

    // Segundo Final
    public LocalDate final2Fecha;
    public String final2Hora;
    public String final2Aula;
    public LocalDate final2RevFecha;
    public String final2RevHora;

    // Comité
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

    // =======================
    // ===== Setters =========
    // =======================

    public void setDepartamento(String val) {
        this.departamento = val;
    }

    public void setNombreAsignatura(String val) {
        this.nombreAsignatura = val;
    }

    public void setSemestre(String val) {
        this.semestre = convertStringToNumber(val);
    }

    public void setSeccion(String val) {
        this.seccion = val;
    }

    public void setTituloProfesor(String val) {
        this.tituloProfesor = val;
    }

    public void setApellidoProfesor(String val) {
        this.apellidoProfesor = val;
    }

    public void setNombreProfesor(String val) {
        this.nombreProfesor = val;
    }

    public void setEmailProfesor(String val) {
        this.emailProfesor = val;
    }

    public void setParcial1Fecha(String val) {
        this.parcial1Fecha = parseDate(val);
    }

    public void setParcial1Hora(String val) {
        this.parcial1Hora = cleanTime(val);
    }

    public void setParcial1Aula(String val) {
        this.parcial1Aula = val;
    }

    public void setParcial2Fecha(String val) {
        this.parcial2Fecha = parseDate(val);
    }

    public void setParcial2Hora(String val) {
        this.parcial2Hora = cleanTime(val);
    }

    public void setParcial2Aula(String val) {
        this.parcial2Aula = val;
    }

    public void setFinal1Fecha(String val) {
        this.final1Fecha = parseDate(val);
    }

    public void setFinal1Hora(String val) {
        this.final1Hora = cleanTime(val);
    }

    public void setFinal1Aula(String val) {
        this.final1Aula = val;
    }

    public void setFinal1RevFecha(String val) {
        this.final1RevFecha = parseDate(val);
    }

    public void setFinal1RevHora(String val) {
        this.final1RevHora = cleanTime(val);
    }

    public void setFinal2Fecha(String val) {
        this.final2Fecha = parseDate(val);
    }

    public void setFinal2Hora(String val) {
        this.final2Hora = cleanTime(val);
    }

    public void setFinal2Aula(String val) {
        this.final2Aula = val;
    }

    public void setFinal2RevFecha(String val) {
        this.final2RevFecha = parseDate(val);
    }

    public void setFinal2RevHora(String val) {
        this.final2RevHora = cleanTime(val);
    }

    public void setComitePresidente(String val) {
        this.comitePresidente = val;
    }

    public void setComiteMiembro1(String val) {
        this.comiteMiembro1 = val;
    }

    public void setComiteMiembro2(String val) {
        this.comiteMiembro2 = val;
    }

    public void setAulaLunes(String val) {
        this.aulaLunes = val;
    }

    public void setLunes(String val) {
        this.lunes = val;
    }

    public void setAulaMartes(String val) {
        this.aulaMartes = val;
    }

    public void setMartes(String val) {
        this.martes = val;
    }

    public void setAulaMiercoles(String val) {
        this.aulaMiercoles = val;
    }

    public void setMiercoles(String val) {
        this.miercoles = val;
    }

    public void setAulaJueves(String val) {
        this.aulaJueves = val;
    }

    public void setJueves(String val) {
        this.jueves = val;
    }

    public void setAulaViernes(String val) {
        this.aulaViernes = val;
    }

    public void setViernes(String val) {
        this.viernes = val;
    }

    public void setAulaSabado(String val) {
        this.aulaSabado = val;
    }

    public void setSabado(String val) {
        this.sabado = val;
    }

    public void setFechasSabadoNoche(String val) {
        this.fechasSabadoNoche = val;
    }

    // ================================
    // ======= Cleaning methods =======
    // ================================

    private static String cleanTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return "";
        }

        String timeStrOriginal = timeStr.trim();

        // Eliminar texto no numérico (excepto : y .)
        String cleaned = timeStr.replaceAll("[^0-9:.]", "").trim();

        try {
            if (cleaned.contains(":")) {
                // Intentar parsear como hh:mm
                String[] segments = cleaned.split(":");
                int hours = segments.length > 0 ? Integer.parseInt(segments[0]) : 0;
                int minutes = segments.length > 1 ? Integer.parseInt(segments[1]) : 0;

                hours = Math.max(0, Math.min(hours, 23));
                minutes = Math.max(0, Math.min(minutes, 59));

                return String.format("%02d:%02d", hours, minutes);
            } else {
                // Parsear como decimal de Excel
                double decimalValue = Double.parseDouble(timeStrOriginal);
                int totalMinutes = (int) Math.round(decimalValue * 24 * 60);
                int hours = (totalMinutes / 60) % 24;
                int minutes = totalMinutes % 60;

                return String.format("%02d:%02d", hours, minutes);
            }
        } catch (NumberFormatException e) {
            System.err.println("Warning: No se pudo parsear la hora -> " + timeStrOriginal);
            return "";
        }
    }

    private static Integer convertStringToNumber(String str) {
        if (str == null || str.isBlank()) {
            return 0;
        }

        try {
            // Reemplazar comas por puntos y remover caracteres no numéricos
            String cleaned = str.replace(',', '.')
                    .replaceAll("[^0-9.-]", "");

            // Verificar si queda algo para parsear
            if (cleaned.isEmpty() || cleaned.equals("-") || cleaned.equals(".")) {
                return 0;
            }

            // Parsear y redondear
            return (int) Math.round(Double.parseDouble(cleaned));

        } catch (Exception e) {
            return 0;
        }
    }

    // DATE cleaner pattern
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "(?i)^(lun|mar|mi[ée]|jue|vie|s[áa]b|dom)?\\s*(\\d{1,2})[/-](\\d{1,2})[/-](\\d{2})$");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    private static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Clean bad patterns
        Matcher matcher = DATE_PATTERN.matcher(value.trim());

        if (matcher.matches()) {
            String day = String.format("%02d", Integer.parseInt(matcher.group(2)));
            String month = String.format("%02d", Integer.parseInt(matcher.group(3)));
            String year = matcher.group(4);

            String formatted = day + "/" + month + "/" + year;

            try {
                return LocalDate.parse(formatted, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        return null;
    }
}
