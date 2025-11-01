package poliplanner.excel.sources;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateStractor {
    private static final Pattern DATE_PATTERN =
            Pattern.compile("(\\d{2})(\\d{2})(\\d{2,4})\\.xlsx?$", Pattern.CASE_INSENSITIVE);

    public static LocalDate extractDateFromFilename(String fileName) {
        Matcher dateMatcher = DATE_PATTERN.matcher(fileName);
        if (dateMatcher.find()) {
            int day = Integer.parseInt(dateMatcher.group(1));
            int month = Integer.parseInt(dateMatcher.group(2));
            String yearStr = dateMatcher.group(3);

            int year;
            if (yearStr.length() == 2) {
                year = 2000 + Integer.parseInt(yearStr);
            } else {
                year = Integer.parseInt(yearStr);
            }

            return LocalDate.of(year, month, day);
        }
        return null;
    }
}
