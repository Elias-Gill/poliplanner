package poliplanner.services;

import java.util.Optional;

import java.text.Normalizer;

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

        String[] splitedName = name.split("-");
        String cleanedName = normalizeSubjectName(splitedName[0]); // tomar todo ANTES del guion

        Optional<SubjectsMetadata> result = metadataRepository.findFirstByNameIgnoreCaseAndCareer_Code(cleanedName,
                career);
        if (!result.isPresent() && splitedName.length > 1) {
            cleanedName = normalizeSubjectName(splitedName[1]); // tomar todo DESPUES del guion
            result = metadataRepository.findFirstByNameIgnoreCaseAndCareer_Code(cleanedName, career);
        }

        return result;
    }

    /**
     * Normaliza completamente un nombre de materia:
     * 1. Elimina (*), (**)
     * 2. Convierte a minúsculas
     * 3. Elimina acentos y caracteres diacríticos
     * 4. Normaliza espacios
     ***/
    private static String normalizeSubjectName(String rawName) {
        if (rawName == null) {
            return null;
        }

        return rawName
                .replaceAll("\\(\\*+\\)", "") // Elimina (*), (**)
                .trim() // Elimina espacios al inicio/final
                .toLowerCase() // Convierte a minúsculas
                .transform(MetadataService::removeDiacritics) // Elimina acentos
                .replaceAll("\\s+", " "); // Normaliza espacios múltiples
    }

    /**
     * Elimina diacríticos y convierte caracteres acentuados a su forma básica
     */
    private static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
