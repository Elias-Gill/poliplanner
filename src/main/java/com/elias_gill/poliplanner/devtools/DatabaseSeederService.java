package com.elias_gill.poliplanner.devtools;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseSeederService {
    private static final Path excelFile = Path.of("src/test/resources/output.csv");

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeederService.class);

    private final ExcelService excelService;
    private final ScheduleService scheduleService;
    private final SheetVersionService versionService;

    private final UserRepository userRepo;
    private final SubjectRepository subjectRepository;

    private final List<JpaRepository<?, ?>> repositories;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void cleanAndSeed() throws Exception {
        this.clean();
        this.seed();
    }

    @Transactional
    public void cleanDatabase() throws Exception {
        clean();
    }

    @Transactional
    public void seedDatabase() throws Exception {
        seed();
    }

    private void clean() throws Exception {
        repositories.forEach(repo -> {
            if (repo instanceof CrudRepository) {
                ((CrudRepository<?, ?>) repo).deleteAll();
            }
        });
    }

    private void seed() throws Exception {
        logger.warn("Cargando los datos semilla desde: {}\n", excelFile.toString());

        // Parsear datos del excel
        try {
            SheetVersion version = versionService.create("VersionPrueba", excelFile.toString());
            excelService.parseAndPersistCsv(excelFile, version);
        } catch (Exception e) {
            System.err.println("No se pudo cargar los datos semilla: \n" + e);
            throw new RuntimeException("La carga de datos del excel ha fallado: ", e);
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

        logger.warn("Datos semilla cargados satisfactoriamente");
    }
}
