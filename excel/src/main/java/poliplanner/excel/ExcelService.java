package poliplanner.excel;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import poliplanner.excel.parser.ExcelParser;
import poliplanner.excel.parser.SubjectCsvDTO;
import poliplanner.excel.parser.SubjectMapper;
import poliplanner.excel.sources.ExcelDownloadSource;
import poliplanner.excel.sources.WebScrapper;
import poliplanner.excel.exception.ExcelPersistenceException;
import poliplanner.excel.exception.ExcelSynchronizationException;
import poliplanner.models.Career;
import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;
import poliplanner.services.CareerService;
import poliplanner.services.SheetVersionService;
import poliplanner.services.SubjectService;

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
    private final ExcelParser parser;

    // Expuesto para el endpoint '/sync/ci'.
    public boolean autonomousExcelSync() {
        try {
            logger.info("Iniciando actualizacion de excel");
            ExcelDownloadSource source = scrapper.findLatestDownloadSource();
            SheetVersion latestVersion = versionService.findLatest();

            if (source == null || !isNewerVersion(latestVersion, source))
                return false;

            logger.info("Descargando latest source: {}", source.toString());
            Path excelFile = source.downloadThisSource();

            logger.info("Descarga exitosa. Iniciando parseo y persistencia");
            parseAndPersistExcel(excelFile, source.url());

            logger.info("Actualizacion exitosa");

            return true;
        } catch (ExcelPersistenceException e) {
            String message = "Error sincronizando el archivo Excel. Se inició el rollback";
            logger.error(message, e);
            throw new ExcelSynchronizationException(message, e);
        } catch (IOException e) {
            String message = "Error descargando el archivo del source remoto.";
            logger.error(message, e);
            throw new ExcelSynchronizationException(message, e);
        }
    }

    // Expuesto para actualizacion manual con el endpoint '/sync'.
    @Transactional
    public void parseAndPersistExcel(Path excelFile, String url) throws ExcelPersistenceException {
        logger.info("Iniciando conversion y parseado del excel");

        SheetVersion version = versionService.create(excelFile.toString(), url);

        Map<String, List<SubjectCsvDTO>> entries = parser.parseExcel(excelFile);

        for (Entry<String, List<SubjectCsvDTO>> entry : entries.entrySet()) {
            String careerName = entry.getKey();
            List<SubjectCsvDTO> subjects = entry.getValue();

            try {
                persistSubjects(careerName, subjects, version);
            } catch (Exception e) {
                logger.error("Error procesando carrera: {}", careerName, e);
                throw new ExcelPersistenceException("Error procesando: " + careerName, e);
            }
        }
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    private void persistSubjects(String careerName, List<SubjectCsvDTO> subjectsCsv, SheetVersion version) {
        Career carrera = careerService.create(careerName, version);

        for (SubjectCsvDTO rawSubject : subjectsCsv) {
            Subject subject = SubjectMapper.mapToSubject(rawSubject);
            subject.setCareer(carrera);
            subjectService.create(subject);
        }
    }

    private boolean isNewerVersion(SheetVersion latestVersion, ExcelDownloadSource source) {
        LocalDate latestVersionDate = latestVersion.getParsedAt().toLocalDate();
        if (source.uploadDate().isBefore(latestVersionDate)) {
            logger.info("Excel ya se encuentra en su ultima version: " + latestVersion.getUrl());
            return false;
        }

        // Si se parsearon el mismo dia, verificar el url de descarga
        if (latestVersion.getUrl().equals(source.url())) {
            logger.info("Excel ya se encuentra en su ultima version: " + latestVersion.getUrl());
            return false;
        }

        return true;
    }
}
