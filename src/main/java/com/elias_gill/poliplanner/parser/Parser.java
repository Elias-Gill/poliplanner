package com.elias_gill.poliplanner.parser;

import java.io.FileReader;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;

public class Parser {
    public static List<Subject> parseFile() {
        String inputFile = "output.csv"; // TODO: Archivo de entrada

        try {
            List<Subject> beans = new CsvToBeanBuilder<Subject>(new FileReader(inputFile))
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
