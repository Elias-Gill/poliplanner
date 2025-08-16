package poliplanner.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import poliplanner.models.Subject;
import poliplanner.models.metadata.SubjectsMetadata;
import poliplanner.repositories.MetadataRepository;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final MetadataRepository metadataRepository;

    public Optional<SubjectsMetadata> find(Subject subject) {
        // Limpiar lo que no sean espacios, numeros, letras o guion
        String name = subject.getNombreAsignatura();
        String career = subject.getCareer().getName();

        String cleanName = name.replaceAll("[^\\p{L}\\p{N} -]", "").trim();
        String cleanCareerCode = name.replaceAll("[^\\p{L}\\p{N} -]", "").trim();

        return metadataRepository.findFirstByNameIgnoreCase(cleanName);
    }
}
