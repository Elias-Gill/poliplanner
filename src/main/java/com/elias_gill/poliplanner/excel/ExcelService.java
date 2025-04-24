package com.elias_gill.poliplanner.excel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.excel.dto.SubjectCsv;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.services.CareerService;
import com.elias_gill.poliplanner.services.SheetVersionService;
import com.elias_gill.poliplanner.services.SubjectService;

import jakarta.transaction.Transactional;

@Service
public class ExcelService {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CareerService careerService;

    @Autowired
    private SheetVersionService versionService;

    @Transactional
    public void SyncronizeExcel() {
        try {
            String url = ExcelHelper.findLatestExcelUrl();

            SheetVersion latestVersion = versionService.findLatest();
            if (latestVersion != null) {
                if (latestVersion.getUrl().equals(url)) {
                    return;
                }
            }

            Path excelFile = ExcelHelper.downloadFile(url);

            persistSubjectsFromExcel(excelFile, url);

        } catch (InterruptedException | IOException | URISyntaxException e) {
            throw new RuntimeException("Error sincronizando Excel. Rollback iniciado", e);
        }
    }

    public void persistSubjectsFromExcel(Path excelFile, String url)
            throws IOException, InterruptedException, URISyntaxException {

        List<Path> sheets = ExcelHelper.convertExcelToCsv(excelFile);

        SheetVersion version = versionService.create(excelFile.toString(), url);

        // Cada "sheet" representa el horario de una carrera diferente
        for (Path sheet : sheets) {
            ExcelHelper.cleanCsv(sheet);
            List<SubjectCsv> subjectscsv = ExcelHelper.extractSubjects(sheet);

            String careerName = sheet.getFileName().toString();
            Career carrera = careerService.create(careerName, version);

            for (SubjectCsv subjectcsv : subjectscsv) {
                Subject subject = SubjectMapper.mapToSubject(subjectcsv);
                subject.setCarrera(carrera);
                subjectService.create(subject);
            }
        }
    }
}
