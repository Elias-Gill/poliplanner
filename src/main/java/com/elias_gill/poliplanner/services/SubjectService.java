package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.repositories.SubjectRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    @Transactional
    public Subject create(Subject subject) {
        return subjectRepository.save(subject);
    }

    /**
     * Groups a list of {@link Subject} objects first by their semester number
     * and then, within each semester, by the subject name.
     *
     * <p>
     * The resulting map has the following structure:
     * </p>
     * 
     * <pre>
     * Map&lt;Integer, Map&lt;String, List&lt;Subject&gt;&gt;&gt;
     * </pre>
     * 
     * <ul>
     * <li>The outer {@code Integer} key represents the semester number.</li>
     * <li>The inner {@code String} key represents the subject name.</li>
     * <li>The innermost {@code List&lt;Subject&gt;} contains all sections (or
     * groups) of the same subject within the same semester.</li>
     * </ul>
     *
     * <p>
     * This is useful for displaying subjects grouped first by semester, and within
     * each semester,
     * grouped by subject name with their respective sections and professors.
     * </p>
     *
     * @param subjects
     *                 the list of {@link Subject} instances to be grouped
     * @return a nested map grouping the subjects by semester and then by subject
     *         name
     */
    public Map<Integer, Map<String, List<Subject>>> findByCareer(Long careerId) {
        // TODO: si es que el id de carrera no se encuentra, lanzar una excepcion
        List<Subject> subjects = subjectRepository.findByCareerIdOrderBySemestreAsc(careerId);

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
}
