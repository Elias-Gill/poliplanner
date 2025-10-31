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

        // Cache
        private String lastRawName1;
        private Optional<SubjectsMetadata> lastResult1;
        private String lastRawName2;
        private Optional<SubjectsMetadata> lastResult2;
        public int cacheHits = 0;


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
         * Busca la metadata de una materia en memoria. Como las materias se procesan en orden
         * alfabetico, entonces se guarda el ultimo metadato procesado, asi no hace falta
         * realizar el re-procesado de strings que esto ocupa mucha cpu.
         */
        public Optional<SubjectsMetadata> findMetadata(Subject subject) {
            String name = subject.getNombreAsignatura();
            if (name == null) {
                logger.error("Subject sin nombre: {}", subject);
                return Optional.empty();
            }

            // Revisión cache
            if (lastRawName1 != null && name.startsWith(lastRawName1)) {
                cacheHits++;
                return lastResult1;
            }
            if (lastRawName2 != null && name.startsWith(lastRawName2)) {
                cacheHits++;
                swapCacheEntries();
                return lastResult2;
            }

            String firstPart = name.contains("-") ? name.substring(0, name.indexOf("-")) : name;
            String cleanedName = normalizeSubjectName(firstPart);

            SubjectsMetadata meta = metadataMap.get(cleanedName);

            if (meta == null && name.contains("-")) {
                String secondPart = name.substring(name.indexOf("-") + 1);
                cleanedName = normalizeSubjectName(secondPart);
                meta = metadataMap.get(cleanedName);
            }

            // Actualizar cache (LRU)
            lastRawName2 = lastRawName1;
            lastResult2 = lastResult1;
            lastRawName1 = name;
            lastResult1 = Optional.ofNullable(meta);

            return lastResult1;
        }

        private static String normalizeSubjectName(String rawName) {
            if (rawName == null) return null;

            StringBuilder sb = new StringBuilder(rawName.length());
            boolean inParenthesis = false;
            boolean skipParenthesis = false;
            boolean lastWasSpace = false;

            for (int i = 0; i < rawName.length(); i++) {
                char c = rawName.charAt(i);

                // detectar inicio de paréntesis con *
                if (c == '(' && i + 1 < rawName.length() && rawName.charAt(i + 1) == '*') {
                    inParenthesis = true;
                    skipParenthesis = true;
                    continue;
                }

                // salir del paréntesis
                if (c == ')' && inParenthesis) {
                    inParenthesis = false;
                    skipParenthesis = false;
                    continue;
                }

                if (skipParenthesis) continue;

                // minuscula
                c = Character.toLowerCase(c);

                // quitar diacríticos en el carácter actual
                c = removeDiacriticChar(c);
                if (c == 0) continue; // si la normalización devolvió nada útil

                // normalizar espacios: no añadir espacio inicial
                if (Character.isWhitespace(c)) {
                    if (!lastWasSpace && sb.length() > 0) {
                        sb.append(' ');
                        lastWasSpace = true;
                    }
                } else {
                    sb.append(c);
                    lastWasSpace = false;
                }
            }

            // trim de los extremos para garantizar eliminación de espacios iniciales y finales
            return sb.toString().trim();
        }

        private static char removeDiacriticChar(char c) {
            String norm = java.text.Normalizer.normalize(String.valueOf(c), java.text.Normalizer.Form.NFD);
            return norm.replaceAll("\\p{M}", "").charAt(0);
        }

        private void swapCacheEntries() {
            String tmpName = lastRawName1;
            Optional<SubjectsMetadata> tmpResult = lastResult1;
            lastRawName1 = lastRawName2;
            lastResult1 = lastResult2;
            lastRawName2 = tmpName;
            lastResult2 = tmpResult;
        }
    }
}
