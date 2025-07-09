package com.elias_gill.poliplanner.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.SheetVersion;

public interface CareerRepository extends JpaRepository<Career, Long> {
    Career findByNameIgnoreCase(String name);

    List<Career> findByVersion(SheetVersion name);
}
