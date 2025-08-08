package com.elias_gill.poliplanner.excel.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.ReadingOptions;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import com.elias_gill.poliplanner.exception.ExcelPersistenceException;
import com.opencsv.bean.CsvToBeanBuilder;

public class ExcelParser {

    // ================================
    // ======== Public API ============
    // ================================

    public static Map<String, List<SubjectCsvDTO>> parseExcel(Path file) {
        // TODO: COMPLETAR
        try {
            prueba();
        } catch (Exception e) {
            System.out.println("Error enorme: " + e.toString());
        }
        return null;
    }

    // =====================================
    // ======== Private methods ============
    // =====================================
    private static void prueba() throws FileNotFoundException, IOException {
        String fn = "/home/elias/Descargas/excel/Planificación de clases y examenes .Segundo Academico 2025version web180725.xlsx";

        InputStream is = new FileInputStream(fn);
        ReadingOptions options = new ReadingOptions(true, true);
        ReadableWorkbook wb = new ReadableWorkbook(is, options);
        Sheet sheet = wb.getSheet(5).orElseThrow();

        for (Row r : sheet.read()) {
            if (r.getCellCount() == 30) {
                String s = String.format("%d: rows=%d cells=%d content=%s", r.getRowNum(), r.getCellCount(),
                        r.getPhysicalCellCount(), r.toString());
                System.out.println(s);
            }
        }
        wb.close();

        // try (InputStream is = new FileInputStream(fn); ReadableWorkbook wb = new
        // ReadableWorkbook(is)) {
        // Sheet sheet = wb.getSheet(5).orElseThrow();
        // for (Row r : sheet.read()) {
        // if (r.getCellCount() == 30) {
        // String s = String.format("%d: rows=%d cells=%d content=%s", r.getRowNum(),
        // r.getCellCount(),
        // r.getPhysicalCellCount(), r.toString());
        // System.out.println(s);
        // }
        // }
        // }
    }
}
