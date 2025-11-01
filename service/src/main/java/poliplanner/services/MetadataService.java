package poliplanner.services;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import poliplanner.models.Subject;
import poliplanner.models.metadata.SubjectsMetadata;
import poliplanner.repositories.MetadataRepository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final MetadataRepository metadataRepository;

    private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    /** Devuelve un buscador de metadata ya cargado para la carrera dada. */
    public MetadataSearcher newMetadataSearcher(String careerCode) {
        return new MetadataSearcher(careerCode);
    }

    /**
     * Clase interna optimizada que encapsula la metadata y permite búsquedas rápidas en memoria con
     * cache eficiente.
     */
    public class MetadataSearcher {
        private final Map<String, LightMetadata> metadataMap;

        // Cache optimizado de 2 elementos (LRU simple)
        private String lastRawName1;
        private Optional<LightMetadata> lastResult1;
        private String lastRawName2;
        private Optional<LightMetadata> lastResult2;
        public int cacheHits = 0;

        public MetadataSearcher(String careerCode) {
            // Cargar solo los campos necesarios para ahorrar memoria
            List<Object[]> results = metadataRepository.findNameAndSemesterByCareerCode(careerCode);

            // HashMap con capacidad inicial optimizada
            metadataMap = new HashMap<>(results.size() * 2);

            for (Object[] result : results) {
                String name = (String) result[0];
                Integer semester = (Integer) result[1];
                // Usar objeto ligero en lugar de entidad completa
                metadataMap.put(name, new LightMetadata(name, semester));
            }

            logger.debug(
                    "Loaded {} metadata entries for career: {}", metadataMap.size(), careerCode);
        }

        /**
         * Busca la metadata de una materia en memoria con cache optimizado. El cache de 2 elementos
         * es muy efectivo porque las materias se procesan en orden alfabético.
         */
        public Optional<SubjectsMetadata> findMetadata(Subject subject) {
            String name = subject.getNombreAsignatura();
            if (name == null) {
                logger.error("Subject sin nombre: {}", subject);
                return Optional.empty();
            }

            // Verificación rápida del cache - solo compara los primeros caracteres
            // Esto es más eficiente que startsWith() completo
            if (lastRawName1 != null && isCacheHit(name, lastRawName1)) {
                cacheHits++;
                return convertToSubjectMetadata(lastResult1);
            }
            if (lastRawName2 != null && isCacheHit(name, lastRawName2)) {
                cacheHits++;
                swapCacheEntries();
                return convertToSubjectMetadata(lastResult2);
            }

            // Cache miss - búsqueda normal
            String firstPart = extractFirstPart(name);
            String cleanedName = normalizeSubjectName(firstPart);

            LightMetadata meta = metadataMap.get(cleanedName);

            // Búsqueda alternativa si no se encuentra en la primera parte
            if (meta == null && name.contains("-")) {
                String secondPart = extractSecondPart(name);
                cleanedName = normalizeSubjectName(secondPart);
                meta = metadataMap.get(cleanedName);
            }

            // Actualizar cache (LRU simple)
            updateCache(name, Optional.ofNullable(meta));

            return convertToSubjectMetadata(Optional.ofNullable(meta));
        }

        /**
         * Verificación optimizada de cache hit. Compara solo los primeros 10 caracteres para mayor
         * velocidad.
         */
        private boolean isCacheHit(String currentName, String cachedName) {
            if (currentName == null || cachedName == null) return false;

            int minLength = Math.min(currentName.length(), cachedName.length());
            int compareLength = Math.min(minLength, 10); // Solo comparar primeros 10 chars

            for (int i = 0; i < compareLength; i++) {
                if (currentName.charAt(i) != cachedName.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        private String extractFirstPart(String name) {
            int dashIndex = name.indexOf('-');
            return dashIndex > 0 ? name.substring(0, dashIndex) : name;
        }

        private String extractSecondPart(String name) {
            int dashIndex = name.indexOf('-');
            return dashIndex > 0 && dashIndex + 1 < name.length()
                    ? name.substring(dashIndex + 1)
                    : name;
        }

        private void updateCache(String name, Optional<LightMetadata> result) {
            lastRawName2 = lastRawName1;
            lastResult2 = lastResult1;
            lastRawName1 = name;
            lastResult1 = result;
        }

        private void swapCacheEntries() {
            String tempName = lastRawName1;
            Optional<LightMetadata> tempResult = lastResult1;
            lastRawName1 = lastRawName2;
            lastResult1 = lastResult2;
            lastRawName2 = tempName;
            lastResult2 = tempResult;
        }

        private Optional<SubjectsMetadata> convertToSubjectMetadata(
                Optional<LightMetadata> lightMeta) {
            return lightMeta.map(
                    meta -> {
                        SubjectsMetadata subjectMeta = new SubjectsMetadata();
                        subjectMeta.setName(meta.name);
                        subjectMeta.setSemester(meta.semester);
                        return subjectMeta;
                    });
        }

        /**
         * Normalización optimizada del nombre de la materia. Reducimos operaciones de string para
         * mejor performance.
         */
        private static String normalizeSubjectName(String rawName) {
            if (rawName == null) return null;

            StringBuilder sb = new StringBuilder(rawName.length());
            boolean inParenthesis = false;
            boolean lastWasSpace = false;

            for (int i = 0; i < rawName.length(); i++) {
                char c = rawName.charAt(i);

                // Manejo optimizado de paréntesis
                if (c == '(' && i + 1 < rawName.length() && rawName.charAt(i + 1) == '*') {
                    inParenthesis = true;
                    i++; // Saltar el '*' también
                    continue;
                }

                if (c == ')' && inParenthesis) {
                    inParenthesis = false;
                    continue;
                }

                if (inParenthesis) continue;

                // Convertir a minúscula y remover diacríticos en una sola operación
                c = Character.toLowerCase(c);
                c = removeDiacriticChar(c);
                if (c == 0) continue;

                // Manejo eficiente de espacios
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

            return sb.toString().trim();
        }

        private static char removeDiacriticChar(char c) {
            // Versión optimizada para caracteres comunes
            switch (c) {
                case 'á':
                    return 'a';
                case 'é':
                    return 'e';
                case 'í':
                    return 'i';
                case 'ó':
                    return 'o';
                case 'ú':
                    return 'u';
                case 'ü':
                    return 'u';
                case 'ñ':
                    return 'n';
                default:
                    if (c > 127) {
                        String norm =
                                java.text.Normalizer.normalize(
                                        String.valueOf(c), java.text.Normalizer.Form.NFD);
                        return norm.replaceAll("\\p{M}", "").charAt(0);
                    }
                    return c;
            }
        }

        /** Clase interna para almacenar metadata */
        private static class LightMetadata {
            final String name;
            final int semester;

            LightMetadata(String name, int semester) {
                this.name = name;
                this.semester = semester;
            }
        }
    }
}
