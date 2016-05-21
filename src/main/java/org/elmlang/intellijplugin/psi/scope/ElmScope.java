package org.elmlang.intellijplugin.psi.scope;

import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.*;

import java.util.*;
import java.util.stream.Stream;

public class ElmScope {
    public static Stream<Optional<ElmLowerCaseId>> scopeFor(ElmLowerCaseId elem) {
        return provideValuesFor(elem.getParent());
    }

    public static Stream<Optional<ElmLowerCaseId>> scopeFor(ElmFile file) {
        return provideValuesFor(file);
    }

    public static Stream<Optional<ElmUpperCaseId>> typesFor(ElmFile file) {
        ElmTypesProvider p = new ElmTypesProvider(file);
        return Stream.generate(p::nextType);
    }

    private static Stream<Optional<ElmLowerCaseId>> provideValuesFor(PsiElement element) {
        ElmValuesProvider p = new ElmValuesProvider(element);
        return Stream.generate(p::nextId);
    }
}
