package poliplanner.excel.sources;

import lombok.RequiredArgsConstructor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class WebScrapper {
    private static final Logger logger = LoggerFactory.getLogger(WebScrapper.class);

    private static final Pattern DIRECT_DOWNLOAD_PATTERN =
            Pattern.compile(".*(?i)(horario|clases|examen(?:es)?|exame|exam)[\\w\\-_.]*\\.xlsx$");
    private static final Pattern GOOGLE_DRIVE_FOLDER_PATTERN =
            Pattern.compile(
                    "^https://drive\\.google\\.com/(?:drive/(?:u/\\d+/)?folders|folders)/[\\w-]+",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern GOOGLE_SPREADSHEET_PATTERN =
            Pattern.compile(
                    "^https://docs\\.google\\.com/spreadsheets/d/[\\w-]+",
                    Pattern.CASE_INSENSITIVE);

    private static final String TARGET_URL =
            "https://www.pol.una.py/academico/horarios-de-clases-y-examenes/";

    private final GoogleDriveHelper googleHelper;

    // ================================
    // ======== Public API ============
    // ================================

    public ExcelDownloadSource findLatestDownloadSource() throws IOException {
        Document doc = Jsoup.connect(TARGET_URL).timeout(10000).get();
        return findLatestDownloadSourceInDoc(doc);
    }

    ExcelDownloadSource findLatestDownloadSourceInDoc(Document doc) {
        List<ExcelDownloadSource> sources = extractSourcesFromDoc(doc);

        if (sources == null || sources.isEmpty()) {
            logger.warn("No sources found");
            return null;
        }

        ExcelDownloadSource latestSource = sources.get(0);
        for (ExcelDownloadSource source : sources) {
            logger.info("Found source: {}", source);
            if (source.uploadDate().isAfter(latestSource.uploadDate())) {
                latestSource = source;
            }
        }

        return latestSource;
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    List<ExcelDownloadSource> extractSourcesFromDoc(Document doc) {
        List<ExcelDownloadSource> sources = new ArrayList<ExcelDownloadSource>();

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (isDirectExcelDownloadUrl(url)) {
                ExcelDownloadSource extractedSource = extractDirectSource(url);
                if (extractedSource != null) {
                    sources.add(extractedSource);
                }
            } else if (isGoogleDriveFolderUrl(url)) {
                List<ExcelDownloadSource> extractedSources = googleHelper.listSourcesInUrl(url);
                if (extractedSources != null && !extractedSources.isEmpty()) {
                    sources.addAll(extractedSources);
                }
            } else if (isGoogleSpreadsheetUrl(url)) {
                ExcelDownloadSource extractedSource =
                        googleHelper.getSourceFromSpreadsheetLink(url);
                if (extractedSource != null) {
                    sources.add(extractedSource);
                }
            }
        }

        return sources;
    }

    private static boolean isDirectExcelDownloadUrl(String url) {
        return DIRECT_DOWNLOAD_PATTERN.matcher(url).matches();
    }

    private static boolean isGoogleDriveFolderUrl(String url) {
        return GOOGLE_DRIVE_FOLDER_PATTERN.matcher(url).find();
    }

    private static boolean isGoogleSpreadsheetUrl(String url) {
        return GOOGLE_SPREADSHEET_PATTERN.matcher(url).find();
    }

    private static ExcelDownloadSource extractDirectSource(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        LocalDate date = DateStractor.extractDateFromFilename(fileName);
        if (date == null) {
            return null;
        }

        return new ExcelDownloadSource(url, fileName, date);
    }
}
