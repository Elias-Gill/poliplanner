package com.elias_gill.poliplanner.excel.sources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public record ExcelDownloadSource(
        String url,
        String fileName,
        LocalDate uploadDate) {

    // FIX: errores de try catch
    public Path downloadThisSource() throws IOException {
        URL url = new URL(this.url);
        InputStream in = url.openStream();

        Path tempFile = Files.createTempFile("horario_", ".xlsx");
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);

        in.close();
        return tempFile;
    }
}
