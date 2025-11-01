package poliplanner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import poliplanner.models.Schedule;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserUsernameOrderByCreatedAtDesc(String username);
}
