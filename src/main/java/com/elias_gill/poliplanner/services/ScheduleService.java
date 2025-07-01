package com.elias_gill.poliplanner.services;

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
        return scheduleRepository.findByUserUsername(user);
    }

    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
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
