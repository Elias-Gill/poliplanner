package com.elias_gill.poliplanner.repositories;

import com.elias_gill.poliplanner.models.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserUsername(String username);
}
