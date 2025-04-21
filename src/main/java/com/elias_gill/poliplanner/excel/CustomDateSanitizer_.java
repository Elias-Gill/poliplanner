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
            // Convertir a formato ISO para java.time
            String[] parts = matcher.group(2).split("/");
            return "20" + parts[2] + "-" + parts[1] + "-" + parts[0];
        }
        return value.trim();
    }

    @Override
    public void setParameterString(String value) {
        defaultValue = value;
    }
}
