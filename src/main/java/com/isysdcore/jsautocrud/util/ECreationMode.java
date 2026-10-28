package com.isysdcore.jsautocrud.util;

/**
 * How the plugin should generate the CRUD layers.
 */
public enum ECreationMode {
    FROM_SCRATCH("Generate everything from scratch"),
    FROM_MODEL("Generate layers for an existing entity"),
    ONLY_REPOSITORIES("Only generate repositories"),
    ONLY_SERVICES("Only generate services"),
    ONLY_CONTROLLERS("Only generate controllers");

    private final String label;

    ECreationMode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
