package com.elias_gill.poliplanner.excel.sources;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2})(\\d{2})(\\d{4})\\.xlsx?$",
            Pattern.CASE_INSENSITIVE);

    public static LocalDate extractDateFromFilename(String fileName) {
        Matcher dateMatcher = DATE_PATTERN.matcher(fileName);
        if (dateMatcher.find()) {
            int day = Integer.parseInt(dateMatcher.group(1));
            int month = Integer.parseInt(dateMatcher.group(2));
            int year = Integer.parseInt(dateMatcher.group(3));

            return LocalDate.of(year, month, day);
        }
        return null;
    }
}
