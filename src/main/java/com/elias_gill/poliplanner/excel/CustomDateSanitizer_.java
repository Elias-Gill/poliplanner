package com.elias_gill.poliplanner.excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.bean.processor.StringProcessor;

public class CustomDateSanitizer_ implements StringProcessor {
    String defaultValue;
    private static final Pattern DATE_PATTERN = Pattern
            .compile("^(Lun|Mar|Mié|Mie|Jue|Vie|Sáb|Sab|Dom) (\\d{2}/\\d{2}/\\d{2})$");

    @Override
    public String processString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        Matcher matcher = DATE_PATTERN.matcher(value.trim());
        if (matcher.matches()) {
            return matcher.group(2); // Devuelve solo la parte de la fecha
        }

        return value.trim(); // Si no es una fecha, devuelve el valor original
    }

    @Override
    public void setParameterString(String value) {
        defaultValue = value;
    }
}
