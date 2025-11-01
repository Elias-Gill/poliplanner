package poliplanner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import poliplanner.models.SheetVersion;

public interface SheetVersionRepository extends JpaRepository<SheetVersion, Long> {
    SheetVersion findFirstByOrderByParsedAtDescIdDesc();
}
