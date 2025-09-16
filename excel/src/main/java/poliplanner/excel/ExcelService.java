package poliplanner.excel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import poliplanner.excel.exception.ExcelPersistenceException;
import poliplanner.excel.exception.ExcelSynchronizationException;
import poliplanner.excel.parser.ExcelParser;
import poliplanner.excel.parser.SubjectcDTO;
import poliplanner.excel.parser.SubjectMapper;
import poliplanner.excel.parser.ExcelParser.ParsingResult;
import poliplanner.excel.sources.ExcelDownloadSource;
import poliplanner.excel.sources.WebScrapper;
import poliplanner.models.Career;
import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;
import poliplanner.models.metadata.SubjectsMetadata;
import poliplanner.services.CareerService;
import poliplanner.services.MetadataService;
import poliplanner.services.SheetVersionService;
import poliplanner.services.SubjectService;
import poliplanner.services.MetadataService.MetadataSearcher;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private final static Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private final SubjectService subjectService;
    private final CareerService careerService;
    private final SheetVersionService versionService;
    private final MetadataService metadataService;

    private final WebScrapper scrapper;
    private final ExcelParser parser;

    /**
     * Expuesto para el endpoint '/sync/ci'.
     * <p>
     * Realiza la busqueda y descarga de la ultima version de excel disponible hasta
     * el momento. De existir, parsea dicho archivo y luego realiza la persistencia
     * de los datos de las materias disponibles.
     */
    public boolean autonomousExcelSync() {
        try {
            logger.info("Iniciando actualizacion de excel");
            ExcelDownloadSource source = scrapper.findLatestDownloadSource();
            SheetVersion latestVersion = versionService.findLatest();

            if (source == null || !isNewerVersion(latestVersion, source))
                return false;

            logger.info("Descargando latest source: {}", source.toString());
            File excelFile = source.downloadThisSource();

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

    /**
     * Expuesto para actualizacion manual con el endpoint '/sync'.
     * <p>
     * Realiza el parseado y persistencia de las hojas del excel proporcionado como
     * parametro.
     */
    @Transactional
    public void parseAndPersistExcel(File excelFile, String url) throws ExcelPersistenceException {
        logger.info("Iniciando conversion y parseado del excel");

        SheetVersion version = versionService.create(excelFile.toString(), url);
        parser.parseExcel(excelFile);

        while (parser.hasSheet()) {
            ParsingResult result = parser.parseCurrentSheet();
            String careerName = result.career;
            List<SubjectcDTO> subjects = result.subjects;

            try {
                persistCareerSubjects(careerName, subjects, version);
            } catch (Exception e) {
                logger.error("Error procesando carrera: {}", careerName, e);
                throw new ExcelPersistenceException("Error procesando: " + careerName, e);
            }
        }
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    /**
     * Persiste todas las asignaturas de una carrera a partir de sus datos en
     * formato DTO, transformándolos en entidades de dominio y completando
     * información faltante con metadata cuando sea posible utilizando el
     * {@link MetadataSearcher}.
     *
     * @param careerName    nombre de la carrera a la que pertenecen las
     *                      asignaturas.
     * @param SubjectcsDTOs lista de asignaturas en formato {@link SubjectcDTO}.
     * @param version       versión de la hoja (plan de estudios) asociada a la
     *                      carrera.
     */
    private void persistCareerSubjects(String careerName, List<SubjectcDTO> subjectsDTOs, SheetVersion version) {
        Career carrera = careerService.create(careerName, version);
        MetadataSearcher metadata = metadataService.newMetadataSearcher(careerName);
        List<Subject> subjects = new ArrayList<>();

        for (SubjectcDTO rawSubject : subjectsDTOs) {
            Subject subject = SubjectMapper.mapToSubject(rawSubject);
            subject.setCareer(carrera);

            // Desambiguar semestre de materia
            if (subject.getSemestre() == 0) {
                Optional<SubjectsMetadata> maybeMeta = metadata.findMetadata(subject);
                if (maybeMeta.isPresent()) {
                    SubjectsMetadata meta = maybeMeta.get();
                    subject.setSemestre(meta.getSemester());
                } else {
                    logger.warn("No metadata found: {} - {}", subject.getNombreAsignatura(),
                            subject.getCareer().getName());
                }
            }

            subjects.add(subject);
        }

        subjectService.bulkCreate(subjects);
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
