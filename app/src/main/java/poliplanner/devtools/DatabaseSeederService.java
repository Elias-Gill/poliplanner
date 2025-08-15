package poliplanner.devtools;

import java.io.File;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import poliplanner.excel.ExcelService;
import poliplanner.models.Subject;
import poliplanner.models.User;
import poliplanner.repositories.SubjectRepository;
import poliplanner.repositories.UserRepository;
import poliplanner.services.ScheduleService;

@Service
@RequiredArgsConstructor
public class DatabaseSeederService {
    private static final File excelFile = Path.of("src/test/resources/testExcel.xlsx").toFile();

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeederService.class);

    private final ExcelService excelService;
    private final ScheduleService scheduleService;

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
            excelService.parseAndPersistExcel(excelFile, "Local Test File");
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
