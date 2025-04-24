package com.elias_gill.poliplanner.devtools;

import java.nio.file.Path;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.elias_gill.poliplanner.excel.ExcelService;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    ExcelService service;

    @Override
    public void run(String... args) {
        if (Arrays.asList(args).contains("--seed")) {
            Path excelFile = Path.of("src/test/resources/testExcel.xlsx");
            System.out.println("Cargando los datos semilla desde: " + excelFile.toString() + "\n");

            try {
                service.persistSubjectsFromExcel(excelFile, excelFile.toString());
            } catch (Exception e) {
                System.out.println("No se pudo cargar los datos semilla: \n" + e);
                System.exit(1);
            }

            System.out.println("Datos semilla cargados satisfactoriamente");
            System.exit(0);
        }
    }
}
