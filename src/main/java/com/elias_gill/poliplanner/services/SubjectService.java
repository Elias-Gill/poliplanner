package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.repositories.SubjectRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public Subject create(Subject subject) {
        return subjectRepository.save(subject);
    }

    public List<Subject> findByCareer(Long careerId) {
        return subjectRepository.findByCareerIdOrderBySemestreAsc(careerId);
    }

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }
}
