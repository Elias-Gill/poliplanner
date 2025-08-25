package poliplanner.repositories;

import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    public List<Subject> findByCareerIdOrderBySemestreAscNombreAsignaturaAsc(Long careerId);

    public Optional<Subject> findFirstByNombreAsignaturaAndSeccionAndCareer_Version(String name,
            String section, SheetVersion version);
}
