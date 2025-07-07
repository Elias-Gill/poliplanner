package com.elias_gill.poliplanner.devtools;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.excel.ExcelService;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.SubjectRepository;
import com.elias_gill.poliplanner.repositories.UserRepository;
import com.elias_gill.poliplanner.services.ScheduleService;
import com.elias_gill.poliplanner.services.SheetVersionService;

import jakarta.transaction.Transactional;

@Service
public class DatabaseSeederService {

    @Autowired
    ExcelService excelService;
    @Autowired
    ScheduleService scheduleService;
    @Autowired
    SheetVersionService versionService;
    @Autowired
    UserRepository userRepo;
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    private List<JpaRepository<?, ?>> repositories;

    @Autowired
    private PasswordEncoder passwordEncoder;

    static final Path excelFile = Path.of("src/test/resources/output.csv");

    @Transactional
    public void cleanAndSeed() throws Exception {
        this.cleanDatabase();
        this.seedDatabase();
    }

    @Transactional
    public void cleanDatabase() {
        repositories.forEach(repo -> {
            if (repo instanceof CrudRepository) {
                ((CrudRepository<?, ?>) repo).deleteAll();
            }
        });
    }

    @Transactional
    public void seedDatabase() throws Exception {
        System.out.println("Cargando los datos semilla desde: " + excelFile.toString() + "\n");

        // Parse Excel data
        try {
            SheetVersion version = versionService.create("VersionPrueba", excelFile.toString());
            excelService.parseAndPersistCsv(excelFile, version);
        } catch (Exception e) {
            System.err.println("No se pudo cargar los datos semilla: \n" + e);
            throw new RuntimeException("Excel data loading failed", e);
        }

        // Cargar materias al horario
        List<Long> subjectIds = subjectRepository.findAll()
                .stream()
                .limit(3)
                .map(Subject::getId)
                .collect(Collectors.toList());

        // Crear usuario de pruebas
        User user = userRepo.findByUsername("pruebas").orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername("pruebas");
            newUser.setPassword(passwordEncoder.encode("123"));
            newUser.setRoles(Set.of("USER"));
            return userRepo.save(newUser);
        });

        // Crear horario con los IDs REALES
        scheduleService.create(user.getUsername(), "Horario de pruebas", subjectIds);

        System.out.println("Datos semilla cargados satisfactoriamente");
    }
}
