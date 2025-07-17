package com.elias_gill.poliplanner.excel.sources;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScrapper {
    private static final Pattern XLS_PATTERN = Pattern.compile(".*\\.xlsx?$", Pattern.CASE_INSENSITIVE);
    private static final String TARGET_URL = "https://www.pol.una.py/academico/horarios-de-clases-y-examenes/";
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2})(\\d{2})(\\d{4})\\.xlsx?$",
            Pattern.CASE_INSENSITIVE);

    public static ExcelDownloadSource findLatestDownloadSource() throws IOException {
        Document doc = Jsoup.connect(TARGET_URL).timeout(10000).get();

        return findLatestDownloadSourceInDoc(doc);
    }

    // TEST: divido en partes para poder escribir tests
    static ExcelDownloadSource findLatestDownloadSourceInDoc(Document doc) {
        List<ExcelDownloadSource> sources = extractSourcesFromDoc(doc);

        if (sources == null || sources.isEmpty()) {
            return null;
        }

        ExcelDownloadSource latestSource = sources.get(0);
        for (ExcelDownloadSource source : sources) {
            if (source.uploadDate().isAfter(latestSource.uploadDate())) {
                latestSource = source;
            }
        }

        return latestSource;
    }

    static List<ExcelDownloadSource> extractSourcesFromDoc(Document doc) {
        List<ExcelDownloadSource> sources = new ArrayList<ExcelDownloadSource>();

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (isDirectExcelDownloadUrl(url)) {
                ExcelDownloadSource aux = extractDirectSource(url);
                if (aux != null) {
                    sources.add(aux);
                }
            } else if (isGoogleDriveFolderUrl(url)) {
                List<ExcelDownloadSource> aux = GoogleDriveHelper.listSourcesInUrl(url);
                if (aux != null && !aux.isEmpty()) {
                    sources.addAll(aux);
                }
            }

            // FIX: el caso donde sea un link directo a la plantilla (url de ejemplo:
            // https://docs.google.com/spreadsheets/d/1-GgXSaTTQxDOmew1H3XRqHVTVghSc7ek/edit?usp=drive_link&ouid=114258085620852093405&rtpof=true&sd=true)
        }

        return sources;
    }

    private static boolean isDirectExcelDownloadUrl(String url) {
        return XLS_PATTERN.matcher(url).matches() && url.toLowerCase().contains("exame");
    }

    private static boolean isGoogleDriveFolderUrl(String url) {
        return url.contains("drive") && url.contains("folders");
    }

    static ExcelDownloadSource extractDirectSource(String url) {
        Matcher dateMatcher = DATE_PATTERN.matcher(url);
        if (dateMatcher.find()) {
            String day = dateMatcher.group(1);
            String month = dateMatcher.group(2);
            String year = dateMatcher.group(3);
            LocalDate date = LocalDate.of(
                    Integer.parseInt(year),
                    Integer.parseInt(month),
                    Integer.parseInt(day));

            String fileName = url.substring(url.lastIndexOf('/') + 1);

            return new ExcelDownloadSource(url, fileName, date);
        } else {
            return null;
        }
    }
}
