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

    // Funciona porque solo queremos desambiguar el semestre en la malla, no nos
    // interesa que otro tipo de datos sobre la materia.
    public Optional<SubjectsMetadata> findMetadata(Subject subject) {
        // Limpiar lo que no sean espacios, numeros, letras o guion
        String name = subject.getNombreAsignatura();
        String career = subject.getCareer().getName();

        // Tomar solo la parte antes del guion y limpiar caracteres no alfanuméricos
        String cleanName = name.split("-")[0] // tomar todo antes del guion
                .replaceAll("[^\\p{L}\\p{N} ]", "") // conservar letras (incluye acentos y ñ), números y espacios
                .trim(); // quitar espacios al inicio/final

        return metadataRepository.findFirstByNameIgnoreCaseAndCareer_Code(cleanName, career);
    }
}
