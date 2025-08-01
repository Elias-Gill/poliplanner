package com.elias_gill.poliplanner.excel;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.excel.parser.ExcelParser;
import com.elias_gill.poliplanner.excel.parser.SubjectCsvDTO;
import com.elias_gill.poliplanner.excel.parser.SubjectMapper;
import com.elias_gill.poliplanner.excel.sources.ExcelDownloadSource;
import com.elias_gill.poliplanner.excel.sources.WebScrapper;
import com.elias_gill.poliplanner.exception.CsvParsingException;
import com.elias_gill.poliplanner.exception.ExcelSynchronizationException;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.services.CareerService;
import com.elias_gill.poliplanner.services.SheetVersionService;
import com.elias_gill.poliplanner.services.SubjectService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private final static Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private final SubjectService subjectService;
    private final CareerService careerService;
    private final SheetVersionService versionService;
    private final WebScrapper scrapper;

    /*
     * Esta función se divide en tres partes para facilitar el testeo de componentes
     * específicos
     * y permitir reutilizar la lógica en herramientas como el seeder para entornos
     * de desarrollo.
     */
    @Transactional
    public Boolean SyncronizeExcel() {
        try {
            logger.info("Iniciando actualizacion de excel");
            ExcelDownloadSource source = scrapper.findLatestDownloadSource();

            SheetVersion latestVersion = versionService.findLatest();
            // Convert Date to LocalDate
            LocalDate latestVersionDate = latestVersion.getParsedAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (latestVersion != null) {
                if (source.uploadDate().isBefore(latestVersionDate)) {
                    logger.info("Excel ya se encuentra en su ultima version: " + latestVersion.getUrl());
                    return false;
                }

                // Si se parsearon el mismo dia, verificar el url de descarga
                if (latestVersion.getUrl().equals(source.url())) {
                    logger.info("Excel ya se encuentra en su ultima version: " + latestVersion.getUrl());
                    return false;
                }
            }

            logger.info("Descargando latest source: {}", source.toString());
            Path excelFile = source.downloadThisSource();

            logger.info("Descarga exitosa. Iniciando parseo y persistencia");
            persistSubjectsFromExcel(excelFile, source.url());

            logger.info("Actualizacion exitosa");

            return true;
        } catch (InterruptedException | IOException e) {
            String message = "Error sincronizando el archivo Excel. Se inició el rollback";
            logger.error(message, e);
            throw new ExcelSynchronizationException(message, e);
        }
    }

    // NOTE: dividio en dos metodos diferentes para poder exponer dos tipos de
    // endpoints. Uno para descarga automatizada ('/sync/ci') y un formulario para
    // actualizacion manual ('/sync').
    @Transactional
    public void persistSubjectsFromExcel(Path excelFile, String url)
            throws IOException, InterruptedException {

        logger.info("Iniciando conversion a csv");
        List<Path> sheets = ExcelParser.convertExcelToCsv(excelFile);
        logger.info("Conversion exitosa");

        SheetVersion version = versionService.create(excelFile.toString(), url);
        // Cada "csv" representa una carrera diferente
        for (Path sheet : sheets) {
            try {
                parseAndPersistCsv(sheet, version);
            } catch (Exception e) {
                logger.error("Error procesando el archivo CSV: {}", sheet, e);
                throw new CsvParsingException("Error procesando el archivo: " + sheet, e);
            }
        }
    }

    // NOTE: publico porque se utiliza dentro del DatabaseSeeder.
    @Transactional
    public void parseAndPersistCsv(Path sheet, SheetVersion version) {
        logger.info("Parseando csv: {}", sheet.toString());

        List<SubjectCsvDTO> subjectscsv = ExcelParser.cleanAndParseCsv(sheet);

        logger.info("Enlazando la carrera y persistiendo");
        String careerName = sheet.getFileName().toString().replace(".csv", "");
        Career carrera = careerService.create(careerName, version);

        for (SubjectCsvDTO subjectcsv : subjectscsv) {
            Subject subject = SubjectMapper.mapToSubject(subjectcsv);
            subject.setCareer(carrera);
            subjectService.create(subject);
        }
    }
}
