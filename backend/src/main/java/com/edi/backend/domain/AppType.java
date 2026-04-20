package com.edi.backend.domain;

public enum AppType {
    MINESWEEPER("Minesweeper"),
    OPENMAP("OpenMap"),
    PAINT("Paint"),
    CONTACTS("Contacts");

    private final String label;

    AppType(String label) { this.label = label; }

    public String label() { return label; }
}
