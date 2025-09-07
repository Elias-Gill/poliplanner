package poliplanner.services;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import poliplanner.models.Subject;
import poliplanner.models.metadata.SubjectsMetadata;
import poliplanner.repositories.MetadataRepository;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final MetadataRepository metadataRepository;

    private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    /**
     * Devuelve un buscador de metadata ya cargado para la carrera dada.
     */
    public MetadataSearcher newMetadataSearcher(String careerCode) {
        return new MetadataSearcher(careerCode);
    }

    /**
     * Clase interna que encapsula la metadata y permite búsquedas rápidas en
     * memoria.
     */
    public class MetadataSearcher {

        private final Map<String, SubjectsMetadata> metadataMap;

        public MetadataSearcher(String careerCode) {
            // Cargar toda la metadata de la carrera de la DB
            List<SubjectsMetadata> allMetadata = metadataRepository.findByCareer_Code(careerCode);

            // Crear un hashmap para consultas
            metadataMap = allMetadata.stream()
                    .collect(Collectors.toMap(
                            m -> m.getName(),
                            m -> m,
                            (a, b) -> {
                                // Se queda con el primero si hay duplicados
                                logger.warn("Duplicado encontrado en tabla de metadatos: {}", a.getName());
                                return a;
                            }));
        }

        /**
         * Busca la metadata de una materia en memoria.
         */
        public Optional<SubjectsMetadata> findMetadata(Subject subject) {
            String name = subject.getNombreAsignatura();
            String[] splitedName = name.split("-");
            String cleanedName = normalizeSubjectName(splitedName[0]);

            SubjectsMetadata meta = metadataMap.get(cleanedName);
            if (meta == null && splitedName.length > 1) {
                cleanedName = normalizeSubjectName(splitedName[1]);
                meta = metadataMap.get(cleanedName);
            }

            return Optional.ofNullable(meta);
        }
    }

    private static String normalizeSubjectName(String rawName) {
        if (rawName == null)
            return null;
        return rawName
                .replaceAll("\\(\\*+\\)", "")
                .trim()
                .toLowerCase()
                .transform(MetadataService::removeDiacritics)
                .replaceAll("\\s+", " ");
    }

    private static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
