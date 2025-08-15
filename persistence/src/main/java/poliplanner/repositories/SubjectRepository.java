package poliplanner.repositories;

import poliplanner.models.Subject;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    public List<Subject> findByCareerIdOrderBySemestreAsc(Long careerId);

    public Optional<Subject> findFirstByNombreAsignaturaAndSeccionOrderByCareer_Version_ParsedAtDesc(String name,
            String section);
}
