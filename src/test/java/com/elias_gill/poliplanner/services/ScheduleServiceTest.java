package com.elias_gill.poliplanner.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.CareerRepository;
import com.elias_gill.poliplanner.repositories.ScheduleRepository;
import com.elias_gill.poliplanner.repositories.SheetVersionRepository;
import com.elias_gill.poliplanner.repositories.SubjectRepository;
import com.elias_gill.poliplanner.repositories.UserRepository;

@SpringBootTest
@Transactional
public class ScheduleServiceTest {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CareerRepository careerRepository;
    @Autowired
    private SheetVersionRepository versionRepository;

    @BeforeEach
    void setUp() {
        clearAllRepositories();
    }

    private void clearAllRepositories() {
        // Orden importante por las relaciones
        scheduleRepository.deleteAllInBatch();
        subjectRepository.deleteAllInBatch();
        careerRepository.deleteAllInBatch();
        versionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @Tag("integration")
    void testMigrationTool_SuccessfulMigration() throws Exception {
        // 1. Primero crear y guardar las versiones
        SheetVersion oldVersion = versionRepository.save(new SheetVersion("2022-01-01", "2022-01-01"));
        SheetVersion newVersion = versionRepository.save(new SheetVersion("2023-01-01", "2023-01-01"));

        // 2. Crear usuarios
        User user = userRepository.save(new User("testuser", "password"));

        // 3. Crear carreras con las versiones ya guardadas
        Career career = careerRepository.save(new Career("Computer Science", oldVersion));
        Career newCareer = careerRepository.save(new Career("Computer Science", newVersion));

        // 4. Crear asignaturas
        Subject oldSubject1 = subjectRepository.save(new Subject("Math", "A", career));
        Subject oldSubject2 = subjectRepository.save(new Subject("Physics", "B", career));
        Subject newSubject1 = subjectRepository.save(new Subject("Math", "A", newCareer));
        Subject newSubject2 = subjectRepository.save(new Subject("Physics", "B", newCareer));

        // 5. Crear horario
        Schedule schedule = scheduleRepository
                .save(new Schedule(user, "Mi horario", List.of(oldSubject1, oldSubject2)));

        // Ejecutar
        List<Subject> notMigrated = scheduleService.migrateSubjects(user.getUsername(), schedule.getId());

        // Verificar
        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        assertTrue(updatedSchedule.getSubjects().contains(newSubject1));
        assertTrue(updatedSchedule.getSubjects().contains(newSubject2));
        assertEquals(2, updatedSchedule.getSubjects().size());
        assertTrue(notMigrated.isEmpty());
    }

    @Test
    @Tag("integration")
    void testMigrationTool_PartialMigration() throws Exception {
        SheetVersion oldVersion = versionRepository.save(new SheetVersion("2022-01-01", "2022-01-01"));
        SheetVersion newVersion = versionRepository.save(new SheetVersion("2023-01-01", "2023-01-01"));
        User user = userRepository.save(new User("testuser", "password"));
        Career career = careerRepository.save(new Career("Computer Science", oldVersion));
        Career newCareer = careerRepository.save(new Career("Computer Science", newVersion));

        Subject oldSubject1 = subjectRepository.save(new Subject("Math", "A", career));
        Subject oldSubject2 = subjectRepository.save(new Subject("Physics", "B", career));
        Subject newSubject1 = subjectRepository.save(new Subject("Math", "A", newCareer));

        Schedule schedule = scheduleRepository
                .save(new Schedule(user, "Mi Horario", List.of(oldSubject1, oldSubject2)));

        scheduleService.migrateSubjects(user.getUsername(), schedule.getId());

        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        assertEquals(2, updatedSchedule.getSubjects().size());
        assertTrue(updatedSchedule.getSubjects().contains(newSubject1));

        boolean hasPhysics = updatedSchedule.getSubjects().stream()
                .anyMatch(s -> s.getNombreAsignatura().equals("Physics") && s.getSeccion().equals("B"));
        assertTrue(hasPhysics);
    }

    @Test
    @Tag("integration")
    void testMigrationTool_UnauthorizedUser() {
        User owner = userRepository.save(new User("owner", "password"));
        User otherUser = userRepository.save(new User("otheruser", "password"));
        Schedule schedule = scheduleRepository.save(new Schedule(owner, "Mi Horario", List.of()));

        Exception exception = assertThrows(BadArgumentsException.class, () -> {
            scheduleService.migrateSubjects(otherUser.getUsername(), schedule.getId());
        });
        assertTrue(exception.getMessage().contains("No tienes autorizacion para modificar este horario"));
    }

    @Test
    @Tag("integration")
    void testMigrationTool_ScheduleNotFound() {
        User user = userRepository.save(new User("testuser", "password"));

        Exception exception = assertThrows(BadArgumentsException.class, () -> {
            scheduleService.migrateSubjects(user.getUsername(), 999L);
        });
        assertTrue(exception.getMessage().contains("Horario con id='999' no existe"));
    }
}
