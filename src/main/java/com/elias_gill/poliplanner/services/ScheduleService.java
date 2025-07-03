package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.ScheduleRepository;
import com.elias_gill.poliplanner.repositories.SubjectRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Schedule> findByUserName(String user) {
        return scheduleRepository.findByUserUsernameOrderByCreatedAtDesc(user);
    }

    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    public void deleteSchedule(Long id) throws InternalServerErrorException, BadArgumentsException {
        Optional<Schedule> optSchedule = scheduleRepository.findById(id);

        if (optSchedule.isPresent()) {
            try {
                scheduleRepository.delete(optSchedule.get());
            } catch (Exception e) {
                throw new InternalServerErrorException("Internal error deleting schedule", e);
            }
        } else {
            throw new BadArgumentsException("Schedule with id='" + id + "' does not exist");
        }
    }

    public Schedule updateList(Long id, List<Long> subjectIds) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        schedule.setMaterias(subjects);

        return scheduleRepository.save(schedule);
    }

    public Schedule updateName(Long id, String newName) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.setDescription(newName);

        return scheduleRepository.save(schedule);
    }

    public Schedule create(User user, String description, List<Long> subjectIds) {
        Schedule aux = new Schedule();
        List<Subject> materias = subjectRepository.findAllById(subjectIds);
        aux.setMaterias(materias);
        aux.setDescription(description);
        aux.setUser(user);

        return scheduleRepository.save(aux);
    }
}
