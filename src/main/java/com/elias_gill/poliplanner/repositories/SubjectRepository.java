package com.elias_gill.poliplanner.repositories;

import com.elias_gill.poliplanner.models.Subject;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    public List<Subject> findByCareerId(Long careerId);
}
