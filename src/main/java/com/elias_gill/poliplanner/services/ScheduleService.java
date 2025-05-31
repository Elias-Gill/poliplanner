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

@Service
@Transactional
public class ScheduleService {

    @Autowired private ScheduleRepository scheduleRepository;

    @Autowired private SubjectRepository subjectRepository;

    public Schedule findByUser(User user) {
        return scheduleRepository.findByUser(user);
    }

    public Schedule updateList(Long id, List<Long> subjectIds) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        schedule.setMaterias(subjects);

        return scheduleRepository.save(schedule);
    }

    public Schedule updateName(Long id, String newName) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.setName(newName);

        return scheduleRepository.save(schedule);
    }

    public Schedule create(User user, String name, List<Long> subjectIds) {
        Schedule aux = new Schedule();
        List<Subject> materias = subjectRepository.findAllById(subjectIds);
        aux.setMaterias(materias);

        return scheduleRepository.save(aux);
    }
}
