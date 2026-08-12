package com.vinylvault.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vinylvault.model.DatabaseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExporterService {

    private static final String PLACEHOLDER = "/* [[DATA_PAYLOAD]] */";
    private static final String TEMPLATE_PATH = "/templates/web_viewer_template.html";

    private final StorageService storage;
    private final ObjectMapper mapper;

    public ExporterService(StorageService storage) {
        this.storage = storage;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Generates dist/index.html by injecting the JSON database into the HTML template.
     *
     * @param outputDir directory where dist/index.html will be written
     * @throws IOException on read/write failure
     */
    public Path export(Path outputDir) throws IOException {
        // 1. Load template from classpath resources
        String template;
        try (InputStream in = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (in == null) {
                throw new IOException("Template not found on classpath: " + TEMPLATE_PATH);
            }
            template = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        // 2. Serialize database to JSON
        DatabaseWrapper db = storage.getDb();
        String json = mapper.writeValueAsString(db);

        // 3. Inject JSON into the placeholder
        String html = template.replace(PLACEHOLDER, json);

        // 4. Write to dist/index.html
        Path distDir = outputDir.resolve("dist");
        Files.createDirectories(distDir);
        Path outFile = distDir.resolve("index.html");
        Files.writeString(outFile, html, StandardCharsets.UTF_8);

        return outFile;
    }

    /** Convenience overload — exports relative to the current working directory. */
    public Path export() throws IOException {
        return export(Paths.get(System.getProperty("user.dir")));
    }
}
