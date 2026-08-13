package com.vinylvault.util;

/**
 * The available colour schemes. Each entry maps a stable id (persisted to
 * disk) and a display label to a stylesheet under /css/themes/.
 */
public enum Theme {

    LIGHT           ("light",           "Light",                   false),
    DARK            ("dark",            "Dark",                    true),
    PURPLE_HAZE     ("purple-haze",     "Purple Haze",             true),
    AUTUMN_EVENING  ("autumn-evening",  "Autumn Evening",          true),
    WINTER_BLUES    ("winter-blues",    "Winter Blues",            true),
    SPRING_BREEZE   ("spring-breeze",   "Spring Breeze",           false),
    SUMMER_SUN      ("summer-sun",      "Summer Sun",              false),
    HIGH_CONTRAST   ("high-contrast",   "High Contrast",           true),
    MONOCHROME      ("monochrome",      "Minimalist Monochrome",   false);

    public static final Theme DEFAULT = DARK;

    private final String id;
    private final String label;
    private final boolean dark;

    Theme(String id, String label, boolean dark) {
        this.id    = id;
        this.label = label;
        this.dark  = dark;
    }

    public String getId()    { return id; }
    public String getLabel() { return label; }
    public boolean isDark()  { return dark; }

    /** Classpath location of this theme's variable definitions. */
    public String getStylesheetPath() {
        return "/css/themes/" + id + ".css";
    }

    /** Resolves a persisted id back to a Theme, falling back to {@link #DEFAULT}. */
    public static Theme fromId(String id) {
        if (id != null) {
            for (Theme t : values()) {
                if (t.id.equalsIgnoreCase(id)) return t;
            }
        }
        return DEFAULT;
    }

    /** Shown in the toolbar picker; the suffix makes the list easier to scan. */
    @Override
    public String toString() {
        String kind = dark ? "Dark" : "Light";
        // The plain Light/Dark themes would otherwise read "Light (Light)".
        return label.equals(kind) ? label : label + " (" + kind + ")";
    }
}
