package org.elmlang.intellijplugin.psi.scope;

import java.util.*;
import java.util.stream.Stream;

public class ElmCoreLibrary {
    public static final String BASICS_MODULE = "Basics";

    private static final Set<String> BUILT_IN_SYMBOLS = new HashSet<String>() {{
        add("Bool");
        add("True");
        add("False");
        add("String");
        add("Char");
        add("Int");
        add("Float");
        add("List");
    }};

    private static final Set<String> IMPLICIT_IMPORTS = new HashSet<String>() {{
        add("Maybe");
        add("Result");
        add("List");
        add("Signal");
        add("Debug");
    }};

    public static boolean isBuiltIn(String typeName) {
        return BUILT_IN_SYMBOLS.contains(typeName);
    }

    public static boolean isImplicitImport(String moduleName) {
        return IMPLICIT_IMPORTS.contains(moduleName);
    }

    public static Stack<String> getImplicitImportsCopy() {
        Stack<String> result = new Stack<>();
        IMPLICIT_IMPORTS.forEach(result::push);
        return result;
    }

    public static Stream<String> getBuiltInSymbols(){
        return BUILT_IN_SYMBOLS.stream();
    }
}
