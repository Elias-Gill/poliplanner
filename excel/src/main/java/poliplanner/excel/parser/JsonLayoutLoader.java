package poliplanner.excel.parser;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.DefaultResourceLoader;
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
    private final String resourcePattern;

    public JsonLayoutLoader() {
        this(new DefaultResourceLoader(), new ObjectMapper(), "classpath:json_layouts/*.json");
    }

    // Constructor para uso de Spring
    public JsonLayoutLoader(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this(resourceLoader, objectMapper, "classpath:json_layouts/*.json");
    }

    // Constructor para uso manual
    public JsonLayoutLoader(ObjectMapper objectMapper, String resourcePattern) {
        this(new DefaultResourceLoader(), objectMapper, resourcePattern);
    }

    // Constructor central
    public JsonLayoutLoader(
            ResourceLoader resourceLoader, ObjectMapper objectMapper, String resourcePattern) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.resourcePattern = resourcePattern;
    }

    // Clase interna que representa el layout cargado
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

    @SuppressWarnings("unchecked")
    public List<Layout> loadJsonLayouts() throws IOException {
        List<Layout> layouts = new ArrayList<>();

        Resource[] resources =
                ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                        .getResources(resourcePattern);

        if (resources.length == 0) {
            System.err.println("No se encontraron archivos JSON en: " + resourcePattern);
            return layouts;
        }

        for (Resource resource : resources) {
            try {
                Map<String, List<Map<String, Object>>> json =
                        objectMapper.readValue(resource.getInputStream(), Map.class);
                List<Map<String, Object>> lista = json.get("lista");

                if (lista == null) {
                    System.err.println(
                            "Formato inválido en: "
                                    + resource.getFilename()
                                    + " - Falta la clave 'lista'");
                    continue;
                }

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

                layouts.add(new Layout(resource.getFilename(), headers, patterns));
            } catch (IOException e) {
                System.err.println(
                        "Error al leer el archivo JSON: "
                                + resource.getFilename()
                                + " - "
                                + e.getMessage());
            }
        }

        return layouts;
    }
}
