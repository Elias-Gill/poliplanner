package com.elias_gill.poliplanner.devtools;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final DatabaseSeederService seederService;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Override
    public void run(String... args) {
        try {
            // NOTE: si no se pone exit 0, el server continuara ejecutandose luego del
            // seeding/cleaning
            if (Arrays.asList(args).contains("--clean") && Arrays.asList(args).contains("--seed")) {
                logger.info("Realizando clean and seed...");
                this.seederService.cleanAndSeed();
                System.exit(0);
            } else if (Arrays.asList(args).contains("--seed")) {
                logger.info("Insertando datos semilla...");
                this.seederService.seedDatabase();
                System.exit(0);
            } else if (Arrays.asList(args).contains("--clean")) {
                logger.info("Limpiando base de datos...");
                this.seederService.cleanDatabase();
                System.exit(0);
            }
        } catch (Exception e) {
            logger.error("No se pudo completar la operacion: " + e.getMessage());
            System.exit(1);
        }
    }
}
