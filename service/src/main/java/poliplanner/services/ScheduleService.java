package poliplanner.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import poliplanner.exception.BadArgumentsException;
import poliplanner.exception.InternalServerErrorException;
import poliplanner.exception.InvalidScheduleException;
import poliplanner.exception.SubjectNotFoundException;
import poliplanner.exception.UserNotFoundException;
import poliplanner.models.Schedule;
import poliplanner.models.Subject;
import poliplanner.models.User;
import poliplanner.repositories.ScheduleRepository;
import poliplanner.repositories.SubjectRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final SubjectRepository subjectRepository;
    private final SheetVersionService sheetVersionService;

    public List<Schedule> findByUserName(String user) {
        return scheduleRepository.findByUserUsernameOrderByCreatedAtDesc(user);
    }

    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Transactional
    public Schedule updateList(Long id, List<Long> subjectIds) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        schedule.setSubjects(subjects);

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule updateName(Long id, String newName) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.setDescription(newName);

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void create(String username, String description, List<Long> subjectIds)
            throws UserNotFoundException, InvalidScheduleException, SubjectNotFoundException, InternalError {

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        if (description == null || description.trim().isEmpty()) {
            throw new InvalidScheduleException("Se debe proporcionar un nombre para el horario");
        }

        if (subjectIds == null || subjectIds.isEmpty()) {
            throw new InvalidScheduleException("Debe seleccionar al menos una materia");
        }

        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        if (subjects.size() != subjectIds.size()) {
            throw new SubjectNotFoundException("Una o más materias no existen");
        }

        try {
            Schedule schedule = new Schedule();
            schedule.setUser(userOpt.get());
            schedule.setDescription(description.trim());
            schedule.setSubjects(subjects);
            schedule.setVersion(sheetVersionService.findLatest());

            scheduleRepository.save(schedule);
        } catch (Exception e) {
            throw new InternalError("Error interno al guardar el horario", e);
        }
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, String username)
            throws InternalServerErrorException, BadArgumentsException {

        Optional<Schedule> optSchedule = scheduleRepository.findById(scheduleId);

        if (optSchedule.isEmpty()) {
            throw new BadArgumentsException("Horario con id='" + scheduleId + "' no existe");
        }

        Schedule schedule = optSchedule.get();

        // Ver que el usuario sea el dueno del horario
        if (!schedule.getUser().getUsername().equals(username)) {
            throw new BadArgumentsException("No tienes autorizacion para eliminar este horario");
        }

        try {
            scheduleRepository.delete(schedule);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error interno del servidor al borrar el horario: ", e);
        }
    }

    @Transactional
    public List<Subject> migrateSubjects(String username, Long scheduleId) {
        // Verificar que el usuario sea dueño del horario
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BadArgumentsException("Horario con id='" + scheduleId + "' no existe"));

        if (!schedule.getUser().getUsername().equals(username)) {
            throw new BadArgumentsException("No tienes autorizacion para modificar este horario");
        }

        // Crear una nueva lista mutable para los subjects actualizados
        List<Subject> updatedSubjects = new ArrayList<>();
        List<Subject> notMigratedSubjects = new ArrayList<>();

        // Migrar cada asignatura
        for (Subject oldSubject : schedule.getSubjects()) {
            String subjectName = oldSubject.getNombreAsignatura();
            String section = oldSubject.getSeccion();

            Subject newSubject = subjectRepository
                    .findFirstByNombreAsignaturaAndSeccionOrderByCareer_Version_ParsedAtDesc(subjectName, section)
                    .orElse(null);

            if (newSubject != null) {
                if (isNewerVersion(newSubject, oldSubject)) {
                    updatedSubjects.add(newSubject);
                } else {
                    updatedSubjects.add(oldSubject);
                }
            } else {
                updatedSubjects.add(oldSubject);
                notMigratedSubjects.add(oldSubject);
            }
        }

        // Actualizar la lista de subjects
        schedule.setSubjects(updatedSubjects);
        schedule.setVersion(sheetVersionService.findLatest());

        scheduleRepository.save(schedule);

        return notMigratedSubjects;
    }

    private boolean isNewerVersion(Subject newSubject, Subject oldSubject) {
        LocalDateTime oldDate = oldSubject.getCareer().getVersion().getParsedAt();
        LocalDateTime newDate = oldSubject.getCareer().getVersion().getParsedAt();

        return newDate.isAfter(oldDate);
    }
}
