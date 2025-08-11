package poliplanner.excel.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonLayoutLoader {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    // Constructor para inyección de dependencias
    public JsonLayoutLoader(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    // Clase para almacenar la información de cada JSON
    public static class Layout {
        public String fileName;
        public List<String> headers;
        public Map<String, List<String>> patterns;

        public Layout(String fileName, List<String> headers, Map<String, List<String>> patterns) {
            this.fileName = fileName;
            this.headers = headers;
            this.patterns = patterns;
        }
    }

    // Función auxiliar para cargar los JSON desde resources/json_layouts
    @SuppressWarnings("unchecked")
    public List<Layout> loadJsonLayouts() throws IOException {
        List<Layout> layouts = new ArrayList<>();

        // Cargar todos los archivos JSON desde la carpeta json_layouts
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                .getResources("classpath:json_layouts/*.json");

        if (resources.length == 0) {
            System.err.println("No se encontraron archivos JSON en json_layouts");
            return layouts;
        }

        for (Resource resource : resources) {
            try {
                // Leer el JSON
                Map<String, List<Map<String, Object>>> json = objectMapper.readValue(
                        resource.getInputStream(), Map.class);
                List<Map<String, Object>> lista = json.get("lista");

                if (lista == null) {
                    System.err.println("Formato inválido en: " + resource.getFilename() + " - Falta la clave 'lista'");
                    continue;
                }

                // Extraer encabezados y patrones
                List<String> headers = new ArrayList<>();
                Map<String, List<String>> patterns = new HashMap<>();
                for (Map<String, Object> entry : lista) {
                    String header = (String) entry.get("encabezado");
                    List<String> headerPatterns = (List<String>) entry.get("patron");

                    if (header != null) {
                        headers.add(header);
                        if (headerPatterns != null) {
                            patterns.put(header, headerPatterns);
                        }
                    }
                }

                // Agregar al resultado
                layouts.add(new Layout(resource.getFilename(), headers, patterns));
            } catch (IOException e) {
                System.err.println("Error al leer el archivo JSON: " + resource.getFilename() + " - " + e.getMessage());
            }
        }

        return layouts;
    }
}
