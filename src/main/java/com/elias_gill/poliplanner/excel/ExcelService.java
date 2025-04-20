package com.elias_gill.poliplanner.excel;

import com.elias_gill.poliplanner.models.*;

import java.io.FileReader;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;

public class ExcelService {

    static void syncExcel() {
        Parser.parseFile();
    }

    private class Parser {
        // Lista todos los subjects de un archivo excel
        static List<Subject> parseFile() {
            String home = System.getProperty("user.home");
            Path inputFile = Paths.get(home, "output.csv").toAbsolutePath();

            try {
                List<Subject> beans = new CsvToBeanBuilder<Subject>(new FileReader(inputFile.toString()))
                        .withType(Subject.class)
                        .build()
                        .parse();
                return beans;
            } catch (Exception e) {
                System.out.println("exploto todito");
                System.out.println(e.toString());
                return null;
            }
        }
    }
}
