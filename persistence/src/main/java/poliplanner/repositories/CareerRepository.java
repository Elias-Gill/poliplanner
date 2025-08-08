package poliplanner.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import poliplanner.models.Career;
import poliplanner.models.SheetVersion;

public interface CareerRepository extends JpaRepository<Career, Long> {
    Career findByNameIgnoreCase(String name);

    List<Career> findByVersion(SheetVersion name);
}
