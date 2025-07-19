package com.elias_gill.poliplanner.excel.sources;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebScrapper {
    private static final Pattern XLS_PATTERN = Pattern.compile(".*\\.xlsx?$", Pattern.CASE_INSENSITIVE);
    private static final String TARGET_URL = "https://www.pol.una.py/academico/horarios-de-clases-y-examenes/";

    private GoogleDriveHelper googleHelper;

    public WebScrapper(GoogleDriveHelper googleHelper) {
        this.googleHelper = googleHelper;
    }

    public ExcelDownloadSource findLatestDownloadSource() throws IOException {
        Document doc = Jsoup.connect(TARGET_URL).timeout(10000).get();

        return findLatestDownloadSourceInDoc(doc);
    }

    // TEST: divido en partes para poder escribir tests
    ExcelDownloadSource findLatestDownloadSourceInDoc(Document doc) {
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

    List<ExcelDownloadSource> extractSourcesFromDoc(Document doc) {
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
                List<ExcelDownloadSource> aux = googleHelper.listSourcesInUrl(url);
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
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        LocalDate date = Utils.extractDateFromFilename(fileName);
        if (date == null) {
            return null;
        }

        return new ExcelDownloadSource(url, fileName, date);
    }
}
