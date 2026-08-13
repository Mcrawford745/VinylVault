package com.vinylvault.service;

import com.vinylvault.util.Theme;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Applies colour schemes to scenes and remembers the user's choice.
 *
 * Every scene gets base.css (structure, no colours) followed by the active
 * theme file (colour variables only). Switching themes swaps the second
 * stylesheet, which JavaFX re-applies live — no restart needed.
 *
 * Registered scenes are tracked so that secondary windows (the Lucky modal,
 * dialogs) restyle along with the main window.
 */
public class ThemeService {

    private static final String BASE_CSS   = "/css/base.css";
    private static final String PREF_FILE  = "settings.properties";
    private static final String PREF_KEY   = "theme";

    private static final ThemeService INSTANCE = new ThemeService();

    /** Shared instance — the theme is global to the application. */
    public static ThemeService getInstance() { return INSTANCE; }

    private final Path prefPath;
    private final List<Scene> scenes = new ArrayList<>();
    private Theme current;

    private ThemeService() {
        prefPath = Paths.get(System.getProperty("user.home"), ".vinylvault", PREF_FILE);
        current  = loadPersisted();
    }

    public Theme getCurrent() { return current; }

    /**
     * Attaches base + theme stylesheets to a scene and keeps it in sync with
     * later theme changes. Safe to call more than once for the same scene.
     */
    public void register(Scene scene) {
        if (scene == null || scenes.contains(scene)) return;
        scenes.add(scene);
        apply(scene);
    }

    /** Stops tracking a scene once its window is gone. */
    public void unregister(Scene scene) {
        scenes.remove(scene);
    }

    /** Switches theme across every registered scene and persists the choice. */
    public void setTheme(Theme theme) {
        if (theme == null || theme == current) return;
        current = theme;
        for (Scene scene : scenes) apply(scene);
        persist(theme);
    }

    private void apply(Scene scene) {
        String base  = resource(BASE_CSS);
        String theme = resource(current.getStylesheetPath());
        // Rebuild wholesale so a previous theme's variables can't linger.
        scene.getStylesheets().setAll(base, theme);
    }

    private String resource(String path) {
        var url = getClass().getResource(path);
        if (url == null) throw new IllegalStateException("Missing stylesheet: " + path);
        return url.toExternalForm();
    }

    // ── Persistence ──

    private Theme loadPersisted() {
        if (!Files.exists(prefPath)) return Theme.DEFAULT;
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(prefPath)) {
            props.load(in);
        } catch (IOException e) {
            return Theme.DEFAULT;  // unreadable prefs are not worth failing startup over
        }
        return Theme.fromId(props.getProperty(PREF_KEY));
    }

    private void persist(Theme theme) {
        Properties props = new Properties();
        props.setProperty(PREF_KEY, theme.getId());
        try {
            Files.createDirectories(prefPath.getParent());
            try (OutputStream out = Files.newOutputStream(prefPath)) {
                props.store(out, "VinylVault preferences");
            }
        } catch (IOException e) {
            System.err.println("Could not save theme preference: " + e.getMessage());
        }
    }
}
