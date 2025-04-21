package com.elias_gill.poliplanner.excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.bean.processor.StringProcessor;

public class CustomDateSanitizer_ implements StringProcessor {
    String defaultValue;
    private static final Pattern DATE_PATTERN = Pattern
            .compile("^(?i)(Lun|Mar|Mi[ée]|Jue|Vie|S[áa]b|Dom)\\s+(\\d{2}/\\d{2}/\\d{2})$");

    @Override
    public String processString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        Matcher matcher = DATE_PATTERN.matcher(value.trim());
        if (matcher.matches()) {
            // Extraer solo la parte de la fecha (19/06/25)
            return matcher.group(2);
        }

        return value.trim();
    }

    @Override
    public void setParameterString(String value) {
        defaultValue = value;
    }
}
