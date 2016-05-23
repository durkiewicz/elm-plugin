package org.elmlang.intellijplugin.psi.references;

public enum ElmReferenceTarget {
    SYMBOL("symbol"),
    MODULE("module");

    private final String displayName;

    ElmReferenceTarget(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
