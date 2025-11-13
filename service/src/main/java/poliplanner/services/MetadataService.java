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

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final MetadataRepository metadataRepository;

    private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    // Devuelve un buscador de metadata cargado para la carrera indicada
    public MetadataSearcher createMetadataSearcher(String careerCode) {
        return new MetadataSearcher(careerCode);
    }

    // Clase interna que mantiene la metadata en memoria y permite búsquedas
    public class MetadataSearcher {
        private final List<LightMetadata> metadataEntries;

        // Cache simple de dos elementos para búsquedas recientes
        private String cachedName1;
        private LightMetadata cachedMetadata1;
        private String cachedName2;
        private LightMetadata cachedMetadata2;
        public int cacheHits = 0;

        public MetadataSearcher(String careerCode) {
            // Cargar solo nombre y semestre para la carrera dada
            List<Object[]> rawResults = metadataRepository.findNameAndSemesterByCareerCode(careerCode);

            metadataEntries = new ArrayList<>(rawResults.size());

            for (Object[] row : rawResults) {
                String name = (String) row[0];
                Integer semester = (Integer) row[1];
                metadataEntries.add(new LightMetadata(name, semester));
            }

            logger.debug("Cargadas {} entradas de metadata para carrera: {}", metadataEntries.size(), careerCode);
        }

        // Busca metadata para una materia, devuelve null si no existe
        public SubjectsMetadata findMetadata(Subject subject) {
            String name = subject.getNombreAsignatura();
            if (name == null) {
                logger.error("Materia sin nombre: {}", subject);
                return null;
            }

            // Revisa cache rápido comparando prefijos
            if (cachedName1 != null && matchesCache(name, cachedName1)) {
                cacheHits++;
                return toSubjectsMetadata(cachedMetadata1);
            }
            if (cachedName2 != null && matchesCache(name, cachedName2)) {
                cacheHits++;
                swapCacheEntries();
                return toSubjectsMetadata(cachedMetadata2);
            }

            // No está en cache, se realiza búsqueda lineal
            int dashIndex = name.indexOf('-');
            String part = (dashIndex > 0) ? name.substring(0, dashIndex) : name;
            String normalized = normalizeName(part);

            LightMetadata found = searchMetadata(normalized);

            // Si no se encontró con la primera parte, se intenta con la segunda
            if (found == null && dashIndex > 0) {
                String secondPart = name.substring(dashIndex + 1);
                normalized = normalizeName(secondPart);
                found = searchMetadata(normalized);
            }

            updateCache(name, found);

            return toSubjectsMetadata(found);
        }

        // Busca en la lista un nombre exacto
        private LightMetadata searchMetadata(String normalizedName) {
            int size = metadataEntries.size();
            for (int i = 0; i < size; i++) {
                LightMetadata meta = metadataEntries.get(i);
                if (meta.name.equals(normalizedName)) {
                    return meta;
                }
            }
            return null;
        }

        // Verifica si el nombre buscado corresponde al cacheado (comparando prefijo)
        private boolean matchesCache(String current, String cached) {
            return cached.startsWith(current);
        }

        // Actualiza la cache con el último resultado buscado
        private void updateCache(String name, LightMetadata meta) {
            cachedName2 = cachedName1;
            cachedMetadata2 = cachedMetadata1;
            cachedName1 = name;
            cachedMetadata1 = meta;
        }

        // Intercambia los valores de cache para mantener el orden LRU
        private void swapCacheEntries() {
            String tempName = cachedName1;
            LightMetadata tempMeta = cachedMetadata1;
            cachedName1 = cachedName2;
            cachedMetadata1 = cachedMetadata2;
            cachedName2 = tempName;
            cachedMetadata2 = tempMeta;
        }

        // Convierte el objeto interno a SubjectsMetadata o retorna null si no existe
        private SubjectsMetadata toSubjectsMetadata(LightMetadata meta) {
            if (meta == null) return null;

            SubjectsMetadata sm = new SubjectsMetadata();
            sm.setName(meta.name);
            sm.setSemester(meta.semester);
            return sm;
        }

        // Normaliza el nombre eliminando acentos, caracteres especiales y espacios extra
        private static String normalizeName(String raw) {
            if (raw == null) return null;

            StringBuilder sb = new StringBuilder(raw.length());
            boolean lastWasSpace = false;

            for (int i = 0; i < raw.length(); i++) {
                char c = raw.charAt(i);

                if (c == '*' || c == '(' || c == ')') {
                    continue;
                }

                c = Character.toLowerCase(c);
                c = removeAccent(c);
                if (c == 0) continue;

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

        // Remueve acentos de los caracteres comunes, para otros usa Normalizer
        private static char removeAccent(char c) {
            switch (c) {
                case 'á': return 'a';
                case 'é': return 'e';
                case 'í': return 'i';
                case 'ó': return 'o';
                case 'ú': return 'u';
                case 'ü': return 'u';
                case 'ñ': return 'n';
                default:
                    if (c > 127) {
                        String norm = java.text.Normalizer.normalize(String.valueOf(c), java.text.Normalizer.Form.NFD);
                        return norm.replaceAll("\\p{M}", "").charAt(0);
                    }
                    return c;
            }
        }

        // Clase interna para almacenar los datos de metadata
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
