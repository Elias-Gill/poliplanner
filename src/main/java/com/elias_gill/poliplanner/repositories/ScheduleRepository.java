package com.elias_gill.poliplanner.repositories;

import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule findByUser(User user);
}
