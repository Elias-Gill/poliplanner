package com.elias_gill.poliplanner.devtools;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            // NOTE: if not exit 0, the server would still run after the seeding/cleaning
            // process
            if (Arrays.asList(args).contains("--clean") && Arrays.asList(args).contains("--seed")) {
                logger.info("Performing database clean and seed...");
                this.seederService.cleanAndSeed();
                System.exit(0);
            } else if (Arrays.asList(args).contains("--seed")) {
                logger.info("Seeding database...");
                this.seederService.seedDatabase();
                System.exit(0);
            } else if (Arrays.asList(args).contains("--clean")) {
                logger.info("Cleaning database...");
                this.seederService.cleanDatabase();
                System.exit(0);
            }
        } catch (Exception e) {
            logger.error("Cannot complete operation: " + e.getMessage());
            System.exit(1);
        }
    }
}
