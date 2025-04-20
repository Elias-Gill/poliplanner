package com.elias_gill.poliplanner;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class PoliPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PoliPlannerApplication.class, args);
    }
}
