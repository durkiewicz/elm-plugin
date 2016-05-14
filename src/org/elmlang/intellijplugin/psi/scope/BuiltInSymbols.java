package org.elmlang.intellijplugin.psi.scope;

import java.util.HashSet;
import java.util.Set;

public class BuiltInSymbols {
    private static final Set<String> BUILT_IN_TYPES = new HashSet<String>() {{
        add("Bool");
        add("String");
        add("Int");
        add("Float");
    }};

    public static boolean isBuiltIn(String typeName) {
        return BUILT_IN_TYPES.contains(typeName);
    }
}
