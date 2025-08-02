package com.elias_gill.poliplanner.excel.parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.elias_gill.poliplanner.exception.CsvParsingException;
import com.opencsv.bean.CsvToBeanBuilder;

public class ExcelParser {
    private static final int GARBAGE_HEADER_LINES = 11;

    public static List<SubjectCsvDTO> cleanAndParseCsv(Path csvFile) throws CsvParsingException {
        try {
            Path cleanedCsv = cleanCsv(csvFile);
            List<SubjectCsvDTO> subjects = extractSubjects(cleanedCsv);
            Files.delete(cleanedCsv);

            return subjects;
        } catch (Exception e) {
            throw new CsvParsingException("No se pudo parsear el archivo", e);
        }
    }

    static List<SubjectCsvDTO> extractSubjects(Path csvFile) throws RuntimeException {
        try {
            List<SubjectCsvDTO> beans = new CsvToBeanBuilder<SubjectCsvDTO>(new FileReader(csvFile.toString()))
                    .withType(SubjectCsvDTO.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            return beans;
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se pudo realizar la conversion del archivo '"
                            + csvFile.toString()
                            + "': "
                            + e);
        }
    }

    // Limpia los encabezados que no nos sirven (primeras 11 lineas) dentro del csv
    // generado del horario. Retorna el path a un archivo temporal el cual es el CSV
    // limpio.
    static Path cleanCsv(Path path) throws IOException {
        // Leer todas las líneas del archivo
        List<String> lines = Files.readAllLines(path);

        // Ignorar encabezados
        List<String> cleanedLines = lines.stream().skip(GARBAGE_HEADER_LINES).collect(Collectors.toList());

        // Generar un archivo temporal el cual parsear
        Path tempFile = Files.createTempFile(path.getFileName().toString(), ".xlsx");
        Files.write(tempFile, cleanedLines);

        return tempFile;
    }

    // Convierte cada "hoja" del archivo en archivos CSV. Retorna la lista de
    // archivos convertidos.
    public static List<Path> convertExcelToCsv(Path excelFile) throws IOException, InterruptedException {
        if (!isSsconvertAvailable()) {
            throw new IOException("ssconvert no está instalado. Necesitas instalar Gnumeric.");
        }

        // Se mantiene el nombre del archivo como prefijo del directorio temporal
        Path outputDir = Files.createTempDirectory(excelFile.getFileName().toString());

        ProcessBuilder pb = new ProcessBuilder(
                "ssconvert",
                "--export-file-per-sheet",
                excelFile.toString(),
                outputDir.resolve("%s.csv").toString());

        pb.redirectErrorStream(true);

        Process process = pb.start();

        // Leer la salida del proceso con manejo explícito de recursos
        String processOutput;
        try (InputStream is = process.getInputStream()) {
            processOutput = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Error en la conversión con ssconvert:\n" + processOutput);
        }

        // Filtrar archivos generados, excluyendo hojas irrelevantes o basura
        try (Stream<Path> files = Files.list(outputDir)) {
            List<Path> result = files
                    .filter(path -> path.toString().endsWith(".csv")
                            && !path.toString().contains("odigos")
                            && !path.toString().contains("ódigos")
                            && !path.toString().contains("Asignaturas")
                            && !path.toString().contains("Homologadas")
                            && !path.toString().contains("Homólogas")
                            && !path.toString().equalsIgnoreCase("códigos"))
                    .sorted()
                    .collect(Collectors.toList());

            if (result.isEmpty()) {
                throw new IOException("ssconvert terminó exitosamente pero no generó archivos CSV válidos.");
            }

            return result;
        }
    }

    static boolean isSsconvertAvailable() {
        try {
            Process p = new ProcessBuilder("ssconvert", "--version").start();
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
