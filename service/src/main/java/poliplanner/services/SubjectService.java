package poliplanner.services;

import poliplanner.models.SheetVersion;
import poliplanner.models.Subject;
import poliplanner.repositories.SubjectRepository;

import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SheetVersionService versionService;

    @PersistenceContext
    private EntityManager entityManager;

    public Subject create(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void bulkCreate(List<Subject> subjects) {
        for (int i = 0; i < subjects.size(); i++) {
            entityManager.persist(subjects.get(i));
            if (i % 100 == 0) { // batch size
                entityManager.flush();
                entityManager.clear();
            }
        }
        // The remaining ones
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Agrupa una lista de objetos {@link Subject} primero por numero de semestre,
     * y luego por nombre de materia dentro de cada semestre.
     *
     * El Map resultante tiene:
     * <p>
     * <ul>
     * <li>La clave externa (Integer) representa el semestre.</li>
     * <li>La clave interna (String) representa el nombre de la materia.</li>
     * <li>La lista contiene todas las secciones o grupos de esa materia en ese</li>
     * semestre.
     * </ul>
     *
     * Esto sirve para mostrar las materias organizadas por semestre, y dentro de
     * cada
     * semestre, agrupadas por nombre con sus secciones y profesores.
     *
     * @param subjects lista de instancias de {@link Subject} a agrupar
     * @return un mapa anidado que agrupa las materias por semestre y luego por
     *         nombre
     */
    public Map<Integer, Map<String, List<Subject>>> findByCareer(Long careerId) {
        // TODO: si es que el id de carrera no se encuentra, lanzar una excepcion
        List<Subject> subjects = subjectRepository.findByCareerIdOrderBySemestreAscNombreAsignaturaAsc(careerId);

        Map<Integer, Map<String, List<Subject>>> grouped = subjects.stream()
                .collect(Collectors.groupingBy(
                        Subject::getSemestre,
                        LinkedHashMap::new,
                        Collectors.groupingBy(
                                Subject::getNombreAsignatura,
                                LinkedHashMap::new,
                                Collectors.toList())));

        return grouped;
    }

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> findAllById(List<Long> ids) {
        return subjectRepository.findAllById(ids);
    }

    public Optional<Subject> getLatestByNameAndSection(String name, String section) {
        SheetVersion latestVersion = versionService.findLatest();
        return subjectRepository
                .findFirstByNombreAsignaturaAndSeccionAndCareer_Version(name, section, latestVersion);
    }
}
