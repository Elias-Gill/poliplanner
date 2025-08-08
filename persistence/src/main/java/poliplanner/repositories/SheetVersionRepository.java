package com.elias_gill.poliplanner.repositories;

import com.elias_gill.poliplanner.models.SheetVersion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetVersionRepository extends JpaRepository<SheetVersion, Long> {
    SheetVersion findFirstByOrderByParsedAtDesc();
}
