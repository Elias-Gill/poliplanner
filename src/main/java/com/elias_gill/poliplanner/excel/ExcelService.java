package com.elias_gill.poliplanner.excel;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import com.elias_gill.poliplanner.excel.dto.SubjectCsv;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.services.CareerService;
import com.elias_gill.poliplanner.services.SubjectService;
import com.opencsv.bean.CsvToBeanBuilder;

public class ExcelService {

    private static final String targetUrl = "https://www.pol.una.py/academico/horarios-de-clases-y-examenes/";

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CareerService careerService;

    public void SyncronizeExcel() {
        try {
            String url = findLatestExcelUrl();

            // FIX: comparar con la ultima version descargada

            Path excelFile = downloadFile(url);
            List<Path> sheets = convertExcelToCsv(excelFile);

            // Cada "sheet" representa el horario de una carrera diferente
            for (Path sheet : sheets) {
                // parse subjects from sheet
                cleanCsv(sheet);
                List<SubjectCsv> subjectscsv = extractSubjects(sheet);

                // map parsed csv to actual models in the database
                Career carrera = careerService.findOrCreate(sheet.getFileName().toString());
                for (SubjectCsv subjectcsv : subjectscsv) {
                    Subject subject = SubjectMapper.mapToSubject(subjectcsv);
                    subject.setCarrera(carrera);
                    subjectService.create(subject);
                }
            }

        } catch (IOException e) {
            // TODO: handle exception
        } catch (URISyntaxException e) {
            // TODO: handle exception
        } catch (InterruptedException e) {
            // TODO: handle exception
        }
    }

    static List<SubjectCsv> extractSubjects(Path csvFile) {
        try {
            List<SubjectCsv> beans = new CsvToBeanBuilder<SubjectCsv>(new FileReader(csvFile.toString()))
                    .withType(SubjectCsv.class)
                    .build()
                    .parse();
            return beans;
        } catch (Exception e) {
            // FIX: mensajes de error y logs
            System.out.println("exploto todito");
            System.out.println(e.toString());
            return null;
        }
    }

    static String findLatestExcelUrl() throws IOException {
        Document doc = Jsoup.connect(targetUrl)
                .timeout(10000)
                .get();

        Pattern datePattern = Pattern.compile("(\\d{2})(\\d{2})(\\d{4})\\.xlsx?$",
                Pattern.CASE_INSENSITIVE);

        String latestUrl = "";
        String latestDate = "00000000";

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (url.toLowerCase().matches(".*\\.xls[x]?$") && url.toLowerCase().contains("exame")) {
                Matcher dateMatcher = datePattern.matcher(url);
                if (dateMatcher.find()) {
                    String fileDate = dateMatcher.group(3) + dateMatcher.group(2) + dateMatcher.group(1);
                    if (fileDate.compareTo(latestDate) > 0) {
                        latestDate = fileDate;
                        latestUrl = url;
                    }
                }
            }
        }

        return latestUrl;
    }

    static Path downloadFile(String fileUrl) throws IOException, URISyntaxException {
        URL url = new URL(fileUrl);
        InputStream in = url.openStream();

        Path tempFile = Files.createTempFile("horario_", ".xlsx");
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);

        in.close();
        return tempFile;
    }

    // Limpia los encabezados que no nos sirven (primeras 11 lineas) dentro del csv
    // generado del horario.
    static void cleanCsv(Path path) throws IOException {
        // Leer todas las líneas del archivo
        List<String> lines = Files.readAllLines(path);

        // Ignorar encabezados
        List<String> cleanedLines = lines.stream()
                .skip(11)
                .collect(Collectors.toList());

        // Sobreescribir el archivo original
        Files.write(path, cleanedLines);
    }

    // Convierte cada "hoja" del archivo en archivos csv. Retorna la lista de
    // archivos convertidos
    static List<Path> convertExcelToCsv(Path excelFile) throws IOException, InterruptedException {
        // Verificar si ssconvert está instalado
        if (!isSsconvertAvailable()) {
            throw new IOException("ssconvert no está instalado. Necesitas instalar Gnumeric.");
        }

        Path outputDir = Files.createTempDirectory(excelFile.getFileName().toString());

        // Convertir todas las "hojas" del documento a csv
        ProcessBuilder pb = new ProcessBuilder(
                "ssconvert",
                "--export-file-per-sheet",
                excelFile.toString(),
                outputDir.toString() + "/%s.csv");

        pb.redirectErrorStream(true);

        Process p = pb.start();
        String processOutput = new String(p.getInputStream().readAllBytes());
        int exitCode = p.waitFor();

        if (exitCode != 0) {
            throw new IOException("Error en la conversión:\n" + processOutput);
        }

        // Listar los archivos generados e ignorar el archivo de codigos de carrera
        try (Stream<Path> files = Files.list(outputDir)) {
            return files.filter(path -> path.toString().endsWith(".csv") &&
                    !path.toString().contains("odigos") &&
                    !path.toString().contains("ódigos") &&
                    !path.toString().contains("Asignaturas") &&
                    !path.toString().contains("Homologadas") &&
                    !path.toString().contains("Homólogas") &&
                    !path.toString().equalsIgnoreCase("códigos")).sorted().collect(Collectors.toList());
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
