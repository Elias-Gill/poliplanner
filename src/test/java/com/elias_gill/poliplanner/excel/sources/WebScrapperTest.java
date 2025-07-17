package com.elias_gill.poliplanner.excel.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class WebScrapperTest {
    private static final String DIRECT_DOWNLOAD_URL = "https://www.pol.una.py/wp-content/uploads/Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx";
    private static final String DRIVE_DOWNLOAD_URL = "https://drive.google.com/uc?export=download&id=1BVbZHZ6w01MLzGYBRBx2mbDkJ3-7QtLZ";

    @Test
    @Tag("unit")
    void testFindLatestExcelUrlFromLocalHtml() throws IOException {
        Path htmlPath = Path.of("src/test/resources/pagina_facultad_sin_drive_folders.html");
        String htmlContent = Files.readString(htmlPath);

        Document doc = Jsoup.parse(htmlContent);
        ExcelDownloadSource latestSource = WebScrapper.findLatestDownloadSourceInDoc(doc);

        assertEquals(DIRECT_DOWNLOAD_URL, latestSource.url());
    }

    @Test
    @Tag("integration")
    void testFindLatestExcelUrlWithDriveFolders() throws IOException {
        assumeTrue(System.getenv("GOOGLE_API_KEY") != null, "Skipping test: GOOGLE_API_KEY not set");

        Path htmlPath = Path.of("src/test/resources/pagina_facultad_con_drive_folders.html");
        String htmlContent = Files.readString(htmlPath);

        Document doc = Jsoup.parse(htmlContent);
        ExcelDownloadSource latestSource = WebScrapper.findLatestDownloadSourceInDoc(doc);

        assertEquals(DRIVE_DOWNLOAD_URL, latestSource.url());
    }
}
