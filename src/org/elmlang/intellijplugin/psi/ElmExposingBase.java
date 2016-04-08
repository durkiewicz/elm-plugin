package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ElmExposingBase extends PsiElement {

    @NotNull
    List<ElmExposedUnion> getExposedUnionList();

    @NotNull
    List<ElmLowerCaseId> getLowerCaseIdList();

    @NotNull
    List<ElmOperatorAsFunction> getOperatorAsFunctionList();

    boolean isExposingAll();

    default Predicate<ElmLowerCaseId> getLowerCaseFilter() {
        if (this.isExposingAll()) {
            return x -> true;
        } else {
            final Set<String> set = this.getLowerCaseIdList().stream()
                    .map(PsiElement::getText)
                    .collect(Collectors.toSet());
            return e -> set.contains(e.getText());
        }
    }
}
