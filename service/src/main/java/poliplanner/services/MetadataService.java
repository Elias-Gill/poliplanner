package poliplanner.services;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import poliplanner.models.Subject;
import poliplanner.models.metadata.SubjectsMetadata;
import poliplanner.repositories.MetadataRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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
        private final List<LightMetadata> metadataList;

        // Cache optimizado de 2 elementos (LRU simple) - SIN Optional overhead
        private String lastRawName1;
        private LightMetadata lastResult1;
        private String lastRawName2;
        private LightMetadata lastResult2;
        public int cacheHits = 0;

        public MetadataSearcher(String careerCode) {
            // Cargar solo los campos necesarios para ahorrar memoria
            List<Object[]> results = metadataRepository.findNameAndSemesterByCareerCode(careerCode);

            // ArrayList con capacidad exacta
            metadataList = new ArrayList<>(results.size());

            for (Object[] result : results) {
                String name = (String) result[0];
                Integer semester = (Integer) result[1];
                metadataList.add(new LightMetadata(name, semester));
            }

            logger.debug(
                    "Loaded {} metadata entries for career: {}", metadataList.size(), careerCode);
        }

        /**
         * Busca la metadata de una materia en memoria con cache optimizado.
         * Sin Optional overhead - retorna null si no encuentra
         */
        public SubjectsMetadata findMetadata(Subject subject) {
            String name = subject.getNombreAsignatura();
            if (name == null) {
                logger.error("Subject sin nombre: {}", subject);
                return null;
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

            // Cache miss - búsqueda lineal optimizada
            int dashIndex = name.indexOf('-');
            String firstPart = (dashIndex > 0) ? name.substring(0, dashIndex) : name;
            String cleanedName = normalizeSubjectName(firstPart);

            LightMetadata meta = findInList(cleanedName);

            // Búsqueda alternativa si no se encuentra en la primera parte
            if (meta == null && dashIndex > 0) {
                String secondPart = name.substring(dashIndex + 1);
                cleanedName = normalizeSubjectName(secondPart);
                meta = findInList(cleanedName);
            }

            // Actualizar cache (LRU simple) - SIN Optional overhead
            updateCache(name, meta);

            return convertToSubjectMetadata(meta);
        }

        /**
         * Verificación optimizada de cache hit. Compara solo los primeros 10 caracteres para mayor
         * velocidad.
         */
        private LightMetadata findInList(String cleanedName) {
            int size = metadataList.size();
            // Loop estándar - más rápido que enhanced for para ArrayList
            for (int i = 0; i < size; i++) {
                LightMetadata meta = metadataList.get(i);
                if (meta.name.equals(cleanedName)) {
                    return meta;
                }
            }
            return null;
        }

        private boolean isCacheHit(String currentName, String cachedName) {
            return cachedName.startsWith(currentName);
        }

        private void updateCache(String name, LightMetadata result) {
            lastRawName2 = lastRawName1;
            lastResult2 = lastResult1;
            lastRawName1 = name;
            lastResult1 = result;
        }

        private void swapCacheEntries() {
            String tempName = lastRawName1;
            LightMetadata tempResult = lastResult1;
            lastRawName1 = lastRawName2;
            lastResult1 = lastResult2;
            lastRawName2 = tempName;
            lastResult2 = tempResult;
        }

        /**
         * Conversión directa sin Optional - retorna null si meta es null
         */
        private SubjectsMetadata convertToSubjectMetadata(LightMetadata lightMeta) {
            if (lightMeta == null) return null;
            
            SubjectsMetadata subjectMeta = new SubjectsMetadata();
            subjectMeta.setName(lightMeta.name);
            subjectMeta.setSemester(lightMeta.semester);
            return subjectMeta;
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

                // Saltar asteriscos y paréntesis
                if (c == '*' || c == '(' || c == ')') {
                    continue;
                }

                // Convertir a minúscula
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
