package org.elmlang.intellijplugin.psi.scope;

import org.elmlang.intellijplugin.psi.*;

import java.util.*;
import java.util.stream.Stream;

public class ElmScope {
    public static Stream<Optional<ElmLowerCaseId>> scopeFor(ElmLowerCaseId elem) {
        ElmValuesProvider p = new ElmValuesProvider(elem.getParent());
        return Stream.generate(p::nextId);
    }

    public static Stream<Optional<ElmUpperCaseId>> typesFor(ElmFile file) {
        ElmTypesProvider p = new ElmTypesProvider(file);
        return Stream.generate(p::nextType);
    }
}
