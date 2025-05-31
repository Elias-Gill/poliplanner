package com.elias_gill.poliplanner.devtools;

import com.elias_gill.poliplanner.excel.ExcelService;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.services.SheetVersionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired ExcelService excelService;

    @Autowired SheetVersionService versionService;

    @Override
    public void run(String... args) {
        if (Arrays.asList(args).contains("--seed")) {
            Path excelFile = Path.of("src/test/resources/output.csv");
            System.out.println("Cargando los datos semilla desde: " + excelFile.toString() + "\n");

            try {
                SheetVersion version =
                        versionService.create(excelFile.toString(), excelFile.toString());
                excelService.parseAndPersistCsv(excelFile, version);
            } catch (Exception e) {
                System.out.println("No se pudo cargar los datos semilla: \n" + e);
                System.exit(1);
            }

            System.out.println("Datos semilla cargados satisfactoriamente");
            System.exit(0);
        }
    }
}
