package com.elias_gill.poliplanner.excel;

import com.elias_gill.poliplanner.exception.CsvParsingException;
import com.elias_gill.poliplanner.exception.ExcelSynchronizationException;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.services.CareerService;
import com.elias_gill.poliplanner.services.SheetVersionService;
import com.elias_gill.poliplanner.services.SubjectService;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CareerService careerService;

    @Autowired
    private SheetVersionService versionService;

    /*
     * Esta función se divide en tres partes para facilitar el testeo de componentes
     * específicos
     * y permitir reutilizar la lógica en herramientas como el seeder para entornos
     * de desarrollo.
     */
    @Transactional
    public void SyncronizeExcel() {
        try {
            logger.info("Iniciando actualizacion de excel");
            String url = ExcelHelper.findLatestExcelUrl();
            SheetVersion latestVersion = versionService.findLatest();

            if (latestVersion != null) {
                if (latestVersion.getUrl().equals(url)) {
                    logger.info("Excel ya se encuentra en su ultima version");
                    return;
                }
            }

            logger.info("Descargando latest url: {}", url);
            Path excelFile = ExcelHelper.downloadFile(url);

            logger.info("Descarga exitosa. Iniciando parseo y persistencia");
            persistSubjectsFromExcel(excelFile, url);
            logger.info("Actualizacion exitosa");
        } catch (InterruptedException | IOException e) {
            String message = "Error sincronizando el archivo Excel. Se inició el rollback";
            logger.error(message, e);
            throw new ExcelSynchronizationException(message, e);
        }
    }

    @Transactional
    public void persistSubjectsFromExcel(Path excelFile, String url)
            throws IOException, InterruptedException {

        logger.info("Iniciando conversion a csv");
        List<Path> sheets = ExcelHelper.convertExcelToCsv(excelFile);
        logger.info("Conversion exitosa");

        SheetVersion version = versionService.create(excelFile.toString(), url);
        // Cada "sheet" representa una carrera diferente
        for (Path sheet : sheets) {
            try {
                parseAndPersistCsv(sheet, version);
            } catch (Exception e) {
                logger.error("Error procesando el archivo CSV: {}", sheet, e);
                throw new CsvParsingException("Error procesando el archivo: " + sheet, e);
            }
        }
    }

    @Transactional
    public void parseAndPersistCsv(Path sheet, SheetVersion version)
            throws IOException {
        logger.info("Parseando csv: {}", sheet.toString());
        logger.info("Limpiando");
        Path cleanedCsv = ExcelHelper.cleanCsv(sheet);

        logger.info("Extrayendo materias");
        List<SubjectCsvDTO> subjectscsv = ExcelHelper.extractSubjects(cleanedCsv);

        logger.info("Enlazando la carrera y persistiendo");
        String careerName = sheet.getFileName().toString().replace(".csv", "");
        Career carrera = careerService.create(careerName, version);

        for (SubjectCsvDTO subjectcsv : subjectscsv) {
            Subject subject = SubjectMapper.mapToSubject(subjectcsv);
            subject.setCareer(carrera);
            subjectService.create(subject);
        }

        // Limpiar archivos temporales
        Files.delete(cleanedCsv);
    }
}
