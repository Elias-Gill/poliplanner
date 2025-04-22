package com.elias_gill.poliplanner.repositories;

import com.elias_gill.poliplanner.models.Career;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CareerRepository extends JpaRepository<Career, Long> {
    Career findByNameIgnoreCase(String name);
}
