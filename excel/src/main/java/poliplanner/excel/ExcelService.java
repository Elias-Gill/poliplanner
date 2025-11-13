package poliplanner.excel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

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
import poliplanner.services.CareerService;
import poliplanner.services.MetadataService;
import poliplanner.services.MetadataService.MetadataSearcher;
import poliplanner.services.SheetVersionService;
import poliplanner.services.SubjectService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private final SubjectService subjectService;
    private final CareerService careerService;
    private final SheetVersionService versionService;
    private final MetadataService metadataService;

    @PersistenceContext private EntityManager entityManager;

    private final WebScrapper scrapper;

    /**
     * Expuesto para el endpoint '/sync/ci'.
     *
     * <p>Realiza la busqueda y descarga de la ultima version de excel disponible hasta el momento.
     * De existir, parsea dicho archivo y luego realiza la persistencia de los datos de las materias
     * disponibles.
     */
    @Transactional
    public boolean autonomousExcelSync() {
        try {
            logger.info("Iniciando actualizacion de excel");
            ExcelDownloadSource source = scrapper.findLatestDownloadSource();
            SheetVersion latestVersion = versionService.findLatest();

            if (source == null || !isNewerVersion(latestVersion, source)) return false;

            logger.info("Descargando latest source: {}", source);
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
     *
     * <p>Realiza el parseado y persistencia de las hojas del excel proporcionado como parametro.
     */
    @Transactional
    public void parseAndPersistExcel(File excelFile, String url) throws ExcelPersistenceException {
        SheetVersion version = versionService.create(excelFile.toString(), url);

        logger.info("Iniciando conversion del archivo Excel");
        logMemoryUsage("Inicio parseAndPersistExcel");

        ExcelParser parser = new ExcelParser();
        parser.parseExcel(excelFile);
        logger.info("Conversion terminada - archivo parseado");

        int totalCacheHits = 0;
        int totalSubjectsProcessed = 0;
        int careerCount = 0;

        // Procesar cada hoja (carrera)
        while (parser.hasSheet()) {
            careerCount++;
            ParsingResult result = parser.parseCurrentSheet();
            String careerName = result.career;
            List<SubjectcDTO> subjects = result.subjects;

            logger.info(
                    "Procesando carrera {}: {} con {} materias",
                    careerCount,
                    careerName,
                    subjects.size());

            try {
                int careerCacheHits = persistCareerSubjects(careerName, subjects, version);
                totalCacheHits += careerCacheHits;
                totalSubjectsProcessed += subjects.size();

                logger.info(
                        "Carrera {} completada - Cache hits: {}/{}",
                        careerName,
                        careerCacheHits,
                        subjects.size(),
                        (careerCacheHits * 100.0 / subjects.size()));

                // Limpieza de memoria después de cada carrera
                cleanUpAfterCareer();

            } catch (Exception e) {
                logger.error("Error procesando carrera: {}", careerName, e);
                throw new ExcelPersistenceException("Error procesando: " + careerName, e);
            }
        }

        logger.info(
                "Procesamiento completado - Total: {} carreras, {} materias, {} cache hits",
                careerCount,
                totalSubjectsProcessed,
                totalCacheHits,
                (totalCacheHits * 100.0 / totalSubjectsProcessed));

        logMemoryUsage("Fin parseAndPersistExcel");
    }

    /**
     * Persiste todas las asignaturas de una carrera con procesamiento por lotes para optimizar
     * memoria y performance.
     */
    private int persistCareerSubjects(
            String careerName, List<SubjectcDTO> subjectsDTOs, SheetVersion version) {
        logMemoryUsage("Inicio persistCareerSubjects: " + careerName);

        Career carrera = careerService.create(careerName, version);

        logger.debug("Cargando metadata para carrera: {}", careerName);
        MetadataSearcher metadata = metadataService.newMetadataSearcher(careerName);
        logger.debug("Metadata cargada");

        int batchSize = 50;
        int cacheHits = 0;
        int subjectsPersisted = 0;

        List<Subject> currentBatch = new ArrayList<>(batchSize);

        for (int i = 0; i < subjectsDTOs.size(); i++) {
            SubjectcDTO rawSubject = subjectsDTOs.get(i);
            Subject subject = SubjectMapper.mapToSubject(rawSubject);
            subject.setCareer(carrera);

            // Desambiguar semestre de materia
            if (subject.getSemestre() == 0) {
                SubjectsMetadata meta = metadata.findMetadata(subject);
                if (meta != null) {
                    subject.setSemestre(meta.getSemester());
                    cacheHits++;
                } else {
                    logger.warn(
                            "No metadata found: {} - {}",
                            subject.getNombreAsignatura(),
                            careerName);
                }
            }

            currentBatch.add(subject);
            subjectsPersisted++;

            // Procesar lote cuando esté lleno o sea el último elemento
            if (currentBatch.size() >= batchSize || i == subjectsDTOs.size() - 1) {
                processBatch(currentBatch, carrera);
                currentBatch.clear();

                // Limpieza periódica del EntityManager
                if (subjectsPersisted % (batchSize * 3) == 0) {
                    cleanEntityManager();
                    // Re-attach career después del clear
                    carrera = entityManager.merge(carrera);
                }
            }
        }

        logger.debug(
                "Persistencia completada para {}: {} materias, {} cache hits",
                careerName,
                subjectsPersisted,
                cacheHits);

        logMemoryUsage("Fin persistCareerSubjects: " + careerName);

        return cacheHits;
    }

    /** Procesa un lote de materias de manera eficiente. */
    private void processBatch(List<Subject> batch, Career carrera) {
        for (Subject subject : batch) {
            entityManager.persist(subject);
        }

        // Flush y clear periódico para liberar memoria
        entityManager.flush();
        entityManager.clear();

        logger.trace("Procesado lote de {} materias", batch.size());
    }

    /** Limpieza del EntityManager para liberar memoria. */
    private void cleanEntityManager() {
        entityManager.flush();
        entityManager.clear();

        // Sugerir garbage collection (no bloqueante)
        if (isMemoryPressureHigh()) {
            System.gc();
        }
    }

    /** Verifica si hay presión alta en la memoria. */
    private boolean isMemoryPressureHigh() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();

        // Considerar presión alta si usamos más del 70% del heap
        return (usedMemory * 100 / maxMemory) > 70;
    }

    /** Limpieza después de procesar una carrera completa. */
    private void cleanUpAfterCareer() {
        // Limpieza exhaustiva del EntityManager
        entityManager.flush();
        entityManager.clear();

        // Forzar garbage collection después de cada carrera
        System.gc();

        // Pequeña pausa para permitir que el GC trabaje
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logMemoryUsage("Después de limpieza de carrera");
    }

    /** Log del uso de memoria para monitoreo. */
    private void logMemoryUsage(String stage) {
        if (logger.isDebugEnabled()) {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            long freeMemory = runtime.freeMemory() / (1024 * 1024);

            logger.debug(
                    "Memoria en {}: Usados={}MB, Libres={}MB, Max={}MB",
                    stage,
                    usedMemory,
                    freeMemory,
                    maxMemory);
        }
    }

    // =====================================
    // ======== Private methods ============
    // =====================================

    private boolean isNewerVersion(SheetVersion latestVersion, ExcelDownloadSource source) {
        LocalDate latestVersionDate = latestVersion.getParsedAt().toLocalDate();
        if (source.uploadDate().isAfter(latestVersionDate)) {
            return true;
        }

        // Si se parsearon el mismo dia, verificar el url de descarga
        if (!latestVersion.getUrl().equals(source.url())) {
            return true;
        }

        logger.info("Excel ya se encuentra en su ultima version: " + latestVersion.toString());
        return false;
    }
}
