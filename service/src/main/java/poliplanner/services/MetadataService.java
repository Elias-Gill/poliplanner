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
            String[] splitedName = name.contains("-") ? name.split("-") : new String[] { name };
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

        // 1. Quitar paréntesis con asteriscos manualmente
        int length = rawName.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = rawName.charAt(i);
            if (c == '(' && i + 1 < length && rawName.charAt(i + 1) == '*') {
                // Saltar hasta el cierre del paréntesis
                while (i < length && rawName.charAt(i) != ')')
                    i++;
            } else {
                sb.append(c);
            }
        }

        // 2. Trim y pasar a minúsculas
        String cleaned = sb.toString().trim().toLowerCase();

        // 3. Quitar diacríticos
        cleaned = removeDiacritics(cleaned);

        // 4. Reemplazar múltiples espacios por uno solo
        sb.setLength(0);
        boolean lastWasSpace = false;
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!lastWasSpace) {
                    sb.append(' ');
                    lastWasSpace = true;
                }
            } else {
                sb.append(c);
                lastWasSpace = false;
            }
        }

        return sb.toString();
    }

    private static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
