package com.elias_gill.poliplanner.excel.sources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public record ExcelDownloadSource(String url, String fileName, LocalDate uploadDate) {
    public Path downloadThisSource() throws IOException {
        URL sourceUrl = new URL(this.url);

        Path tempFile = Files.createTempFile("horario_" + this.fileName() + "__", ".xlsx");
        try (InputStream in = sourceUrl.openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return tempFile;
    }
}
