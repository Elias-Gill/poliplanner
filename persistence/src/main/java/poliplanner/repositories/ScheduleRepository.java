package poliplanner.repositories;

import poliplanner.models.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserUsernameOrderByCreatedAtDesc(String username);
}
