package com.elias_gill.poliplanner.excel.sources;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
public class GoogleDriveHelper {
    private static final String SEARCH_URL_FORMAT = "https://www.googleapis.com/drive/v3/files?q='%s'+in+parents&key=%s";
    private static final String DOWNLOAD_URL_FORMAT = "https://drive.google.com/uc?export=download&id=%s";
    private static final Pattern FOLDER_ID_PATTERN = Pattern.compile("folders/([a-zA-Z0-9_-]+)");

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String GOOGLE_API_KEY = System.getenv("GOOGLE_API_KEY");

    public List<ExcelDownloadSource> listSourcesInUrl(String url) {
        String folderId = extractFolderId(url);
        if (folderId == null)
            return null;

        try {
            List<Map<String, Object>> files = listFilesInFolder(folderId);
            List<ExcelDownloadSource> sources = new ArrayList<>();

            for (Map<String, Object> file : files) {
                String id = (String) file.get("id");
                String name = (String) file.get("name");

                if (name != null && name.toLowerCase().endsWith(".xlsx")) {
                    String downloadUrl = String.format(DOWNLOAD_URL_FORMAT, id);
                    LocalDate fileDate = Utils.extractDateFromFilename(name);
                    if (fileDate == null) {
                        continue;
                    }
                    sources.add(new ExcelDownloadSource(downloadUrl, name, fileDate));
                }
            }
            return sources;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al consultar la API de Google Drive: " + e.getMessage(), e);
        }
    }

    private String extractFolderId(String url) {
        Matcher matcher = FOLDER_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // NOTE: unchecked porque trabajamos con json
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> listFilesInFolder(String folderId)
            throws IOException, InterruptedException {

        String url = String.format(SEARCH_URL_FORMAT, folderId, GOOGLE_API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Map<String, Object> json = objectMapper.readValue(response.body(), Map.class);
        return (List<Map<String, Object>>) json.getOrDefault("files", List.of());
    }
}
