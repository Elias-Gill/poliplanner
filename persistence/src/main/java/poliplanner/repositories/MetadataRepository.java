package poliplanner.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import poliplanner.models.metadata.SubjectsMetadata;

public interface MetadataRepository extends JpaRepository<SubjectsMetadata, Long> {
    Optional<SubjectsMetadata> findFirstByNameIgnoreCase(String name);
}
