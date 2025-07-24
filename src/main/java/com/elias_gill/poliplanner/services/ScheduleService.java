package com.elias_gill.poliplanner.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.exception.InvalidScheduleException;
import com.elias_gill.poliplanner.exception.SubjectNotFoundException;
import com.elias_gill.poliplanner.exception.UserNotFoundException;
import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.ScheduleRepository;
import com.elias_gill.poliplanner.repositories.SubjectRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final SubjectRepository subjectRepository;

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
    public List<Subject> migrateSubjects(Long scheduleId, String username) {
        // Ver que el usuario sea el dueno del horario
        Optional<Schedule> optSchedule = scheduleRepository.findById(scheduleId);
        if (optSchedule.isEmpty()) {
            throw new BadArgumentsException("Horario con id='" + scheduleId + "' no existe");
        }

        Schedule schedule = optSchedule.get();
        if (!schedule.getUser().getUsername().equals(username)) {
            throw new BadArgumentsException("No tienes autorizacion para modificar este horario");
        }

        // Comenzar migracion
        List<Subject> subjects = schedule.getSubjects();
        List<Subject> notMigratedSubjects = new ArrayList<Subject>();

        for (int i = 0; i < subjects.size(); i++) {
            String subjectName = subjects.get(i).getNombreAsignatura();

            Subject newSubject = subjectRepository
                    .findFirstByNombreAsignaturaOrderByCareer_Version_ParsedAtDesc(subjectName)
                    .orElse(null);

            if (newSubject != null) {
                subjects.set(i, newSubject);
            } else {
                notMigratedSubjects.add(newSubject);
            }
        }

        schedule.setSubjects(subjects);
        scheduleRepository.save(schedule);

        return notMigratedSubjects;
    }
}
