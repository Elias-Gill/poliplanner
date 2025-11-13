package poliplanner.excel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import poliplanner.excel.exception.ExcelPersistenceException;
import poliplanner.excel.exception.ExcelSynchronizationException;
import poliplanner.excel.parser.ExcelParser;
import poliplanner.excel.parser.ExcelParser.ParsingResult;
import poliplanner.excel.parser.SubjectMapper;
import poliplanner.excel.parser.SubjectcDTO;
import poliplanner.excel.sources.ExcelDownloadSource;
import poliplanner.excel.sources.WebScrapper;
import poliplanner.models.Career;
import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;
import poliplanner.models.metadata.SubjectsMetadata;
import poliplanner.services.MetadataService;
import poliplanner.services.MetadataService.MetadataSearcher;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private final MetadataService metadataService;
    private final WebScrapper scrapper;
    private final SessionFactory sessionFactory;

    @Transactional
    public boolean autonomousExcelSync() {
        try {
            logger.info("Iniciando actualización de Excel");

            ExcelDownloadSource source = scrapper.findLatestDownloadSource();
            if (source == null) {
                logger.warn("No se encontró fuente de descarga de Excel");
                return false;
            }

            SheetVersion latestVersion = findLatestVersion();
            if (latestVersion != null && !isNewerVersion(latestVersion, source)) {
                logger.info("Excel ya está actualizado: {}", latestVersion);
                return false;
            }

            logger.info("Descargando nueva fuente: {}", source);
            File excelFile = source.downloadThisSource();

            logger.info("Descarga exitosa. Iniciando parseo y persistencia");
            parseAndPersistExcel(excelFile, source.url());

            logger.info("Actualización de Excel completada");
            return true;

        } catch (ExcelPersistenceException e) {
            logger.error("Error sincronizando archivo Excel, rollback iniciado", e);
            throw new ExcelSynchronizationException("Error sincronizando archivo Excel", e);
        } catch (IOException e) {
            logger.error("Error descargando archivo desde fuente remota", e);
            throw new ExcelSynchronizationException("Error descargando archivo desde fuente remota", e);
        }
    }

    @Transactional
    public void parseAndPersistExcel(File excelFile, String url) throws ExcelPersistenceException {
        long totalParseTime = 0;
        long totalMetadataTime = 0;
        long totalPersistTime = 0;

        long parseStartOverall = System.currentTimeMillis();
        ExcelParser parser = new ExcelParser();
        parser.parseExcel(excelFile);
        long parseEndOverall = System.currentTimeMillis();
        totalParseTime += (parseEndOverall - parseStartOverall);

        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();

        try {
            SheetVersion version = new SheetVersion(excelFile.getName(), url);
            session.insert(version);

            int totalCacheHits = 0;
            int totalSubjects = 0;
            int careerCount = 0;

            while (parser.hasSheet()) {
                careerCount++;
                ParsingResult result = parser.parseCurrentSheet();
                String careerName = result.career;
                List<SubjectcDTO> subjectsDTOs = result.subjects;

                logger.info("Procesando carrera {}: {} con {} materias", careerCount, careerName, subjectsDTOs.size());

                long metadataStart = System.currentTimeMillis();
                List<Subject> subjects = enrichSubjectsWithMetadata(careerName, subjectsDTOs);
                long metadataEnd = System.currentTimeMillis();
                totalMetadataTime += (metadataEnd - metadataStart);

                long persistStart = System.currentTimeMillis();
                PersistResult persistResult = persistCareerAndSubjects(session, careerName, subjects, version);
                long persistEnd = System.currentTimeMillis();
                totalPersistTime += (persistEnd - persistStart);

                totalCacheHits += persistResult.cacheHits;
                totalSubjects += subjects.size();

                logger.info("Carrera {} completada - cache hits: {} / {} ({}%)",
                        careerName,
                        persistResult.cacheHits,
                        subjects.size(),
                        String.format("%.2f", (persistResult.cacheHits * 100.0 / subjects.size())));
            }

            tx.commit();

            logger.info("Procesamiento finalizado - carreras: {}, materias: {}, cache hits: {} ({}%)",
                    careerCount,
                    totalSubjects,
                    totalCacheHits,
                    String.format("%.2f", (totalCacheHits * 100.0 / totalSubjects)));

            logger.info("Tiempo total parseo: {} ms", totalParseTime);
            logger.info("Tiempo total metadata: {} ms", totalMetadataTime);
            logger.info("Tiempo total persistencia: {} ms", totalPersistTime);

        } catch (Exception e) {
            tx.rollback();
            throw new ExcelPersistenceException("Error durante persistencia del Excel", e);
        } finally {
            session.close();
        }
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    private List<Subject> enrichSubjectsWithMetadata(String careerName, List<SubjectcDTO> subjectsDTOs) {
        MetadataSearcher metadataSearcher = metadataService.createMetadataSearcher(careerName);
        List<Subject> subjects = new ArrayList<>(subjectsDTOs.size());
        int cacheHits = 0;

        for (SubjectcDTO dto : subjectsDTOs) {
            Subject subject = SubjectMapper.mapToSubject(dto);

            if (subject.getSemestre() == 0) {
                SubjectsMetadata meta = metadataSearcher.findMetadata(subject);
                if (meta != null) {
                    subject.setSemestre(meta.getSemester());
                    cacheHits++;
                }
            }

            subjects.add(subject);
        }

        logger.info("Metadatos completados para carrera {} - cache hits: {}", careerName, cacheHits);
        return subjects;
    }

    private static class PersistResult {
        int cacheHits;
    }

    private PersistResult persistCareerAndSubjects(
            StatelessSession session, String careerName, List<Subject> subjects, SheetVersion version) throws ExcelPersistenceException {

        PersistResult result = new PersistResult();

        try {
            Career career = new Career();
            career.setName(careerName);
            career.setVersion(version);
            session.insert(career);

            int cacheHits = 0;
            for (Subject subject : subjects) {
                subject.setCareer(career);
                session.insert(subject);

                if (subject.getSemestre() > 0) {
                    cacheHits++;
                }
            }

            result.cacheHits = cacheHits;
            logger.info("Carrera {} - materias persistidas: {}", careerName, subjects.size());
            return result;

        } catch (Exception e) {
            throw new ExcelPersistenceException("Error procesando carrera: " + careerName, e);
        }
            }

    private SheetVersion findLatestVersion() {
        try (StatelessSession session = sessionFactory.openStatelessSession()) {
            return session
                .createQuery("FROM SheetVersion ORDER BY parsedAt DESC, id DESC", SheetVersion.class)
                .setMaxResults(1)
                .uniqueResult();
        }
    }

    private boolean isNewerVersion(SheetVersion latestVersion, ExcelDownloadSource source) {
        LocalDate latestDate = latestVersion.getParsedAt().toLocalDate();
        return source.uploadDate().isAfter(latestDate)
            || !latestVersion.getUrl().equals(source.url());
    }
}
