package poliplanner.repositories;

import poliplanner.models.SheetVersion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetVersionRepository extends JpaRepository<SheetVersion, Long> {
    SheetVersion findFirstByOrderByParsedAtDesc();
}
