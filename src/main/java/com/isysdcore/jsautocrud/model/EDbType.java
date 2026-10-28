package com.isysdcore.jsautocrud.model;

/**
 * Persistence technology supported by generic-auto-crud.
 */
public enum EDbType {
    SQL("SQL (JPA - PostgreSQL, MySQL, H2, ...)"),
    MONGODB("MongoDB (NoSQL)");

    private final String label;

    EDbType(String label) {
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
