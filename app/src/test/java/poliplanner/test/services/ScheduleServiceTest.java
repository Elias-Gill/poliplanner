package poliplanner.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import poliplanner.exception.BadArgumentsException;
import poliplanner.models.Career;
import poliplanner.models.Schedule;
import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;
import poliplanner.models.User;
import poliplanner.repositories.CareerRepository;
import poliplanner.repositories.ScheduleRepository;
import poliplanner.repositories.SheetVersionRepository;
import poliplanner.repositories.SubjectRepository;
import poliplanner.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@Rollback
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceTest {
    final private ScheduleService scheduleService;
    final private ScheduleRepository scheduleRepository;
    final private SubjectRepository subjectRepository;
    final private UserRepository userRepository;
    final private CareerRepository careerRepository;
    final private SheetVersionRepository versionRepository;

    @Test
    void testMigrationTool_SuccessfulMigration() {
        SheetVersion oldVersion = versionRepository.save(new SheetVersion("2022-01-01", "2022-01-01"));
        SheetVersion newVersion = versionRepository.save(new SheetVersion("2023-01-01", "2023-01-01"));
        versionRepository.flush();

        User user = userRepository.save(new User("testuser", "password"));
        Career oldCareer = careerRepository.save(new Career("Computer Science", oldVersion));
        Career newCareer = careerRepository.save(new Career("Computer Science", newVersion));

        Subject oldSubject1 = subjectRepository.save(new Subject("Math", "A", oldCareer));
        Subject oldSubject2 = subjectRepository.save(new Subject("Physics", "B", oldCareer));
        Subject newSubject1 = subjectRepository.save(new Subject("Math", "A", newCareer));
        Subject newSubject2 = subjectRepository.save(new Subject("Physics", "B", newCareer));

        Schedule schedule = scheduleRepository
                .save(new Schedule(user, "Mi horario", List.of(oldSubject1, oldSubject2), oldVersion));
        scheduleRepository.flush();

        List<Subject> notMigrated = scheduleService.migrateSubjects(user.getUsername(), schedule.getId());

        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId())
                .orElseThrow(() -> new AssertionError("No se encontró el horario actualizado"));
        assertEquals(2, updatedSchedule.getSubjects().size(), "El horario debe contener exactamente 2 asignaturas");
        assertTrue(updatedSchedule.getSubjects().contains(newSubject1),
                "El horario debe contener la versión nueva de Math");
        assertTrue(updatedSchedule.getSubjects().contains(newSubject2),
                "El horario debe contener la versión nueva de Physics");
        assertEquals(newVersion, updatedSchedule.getVersion(),
                "El horario debe estar asociado a la versión más reciente");
        assertTrue(notMigrated.isEmpty(), "No debe haber asignaturas no migradas");
    }

    @Test
    void testMigrationTool_PartialMigration() {
        SheetVersion oldVersion = versionRepository
                .save(new SheetVersion("old file", "old_url"));
        SheetVersion newVersion = versionRepository.save(new SheetVersion("new file", "new_url"));
        versionRepository.flush();

        User user = userRepository.save(new User("testuser", "password"));

        Career oldCareer = careerRepository.save(new Career("Computer Science", oldVersion));
        Career newCareer = careerRepository.save(new Career("Computer Science", newVersion));

        Subject mathV1 = subjectRepository.save(new Subject("Math", "A", oldCareer));
        Subject physicsV1 = subjectRepository.save(new Subject("Physics", "B", oldCareer));

        Subject mathV2 = subjectRepository.save(new Subject("Math", "A", newCareer));

        Schedule schedule = scheduleRepository
                .save(new Schedule(user, "Mi Horario", List.of(mathV1, physicsV1), oldVersion));
        scheduleRepository.flush();

        scheduleService.migrateSubjects(user.getUsername(), schedule.getId());
        scheduleRepository.flush();

        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId())
                .orElseThrow(() -> new AssertionError("No se encontró el horario actualizado"));
        assertEquals(2, updatedSchedule.getSubjects().size(), "El horario debe contener exactamente 2 asignaturas");
        boolean migratedMath = updatedSchedule.getSubjects().stream()
                .anyMatch(s -> s.getId().equals(mathV2.getId()));
        assertTrue(migratedMath, "La asignatura Math debe haberse migrado a la versión más reciente");
        boolean hasPhysics = updatedSchedule.getSubjects().stream()
                .anyMatch(s -> s.getNombreAsignatura().equals("Physics") && s.getSeccion().equals("B"));
        assertTrue(hasPhysics, "La asignatura Physics debe mantenerse en su versión original");
    }

    @Test
    void testMigrationTool_UnauthorizedUser() {
        // Arrange
        User owner = userRepository.save(new User("owner", "password"));
        User otherUser = userRepository.save(new User("otheruser", "password"));
        SheetVersion version = versionRepository.save(new SheetVersion("default", "default_url"));
        Schedule schedule = scheduleRepository.save(new Schedule(owner, "Mi Horario", List.of(), version));
        scheduleRepository.flush();

        // Act & Assert
        Exception exception = assertThrows(BadArgumentsException.class,
                () -> scheduleService.migrateSubjects(otherUser.getUsername(), schedule.getId()),
                "Debe lanzarse BadArgumentsException para usuario no autorizado");
        assertTrue(exception.getMessage().contains("No tienes autorizacion para modificar este horario"),
                "El mensaje de la excepción debe indicar falta de autorización");
    }

    @Test
    void testMigrationTool_ScheduleNotFound() {
        // Arrange
        User user = userRepository.save(new User("testuser", "password"));
        userRepository.flush();

        // Act & Assert
        Exception exception = assertThrows(BadArgumentsException.class,
                () -> scheduleService.migrateSubjects(user.getUsername(), 999L),
                "Debe lanzarse BadArgumentsException para un horario inexistente");
        assertTrue(exception.getMessage().contains("Horario con id='999' no existe"),
                "El mensaje de la excepción debe indicar que el horario no existe");
    }
}
