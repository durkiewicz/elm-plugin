package org.elmlang.intellijplugin.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.utils.TypeFilter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    default TypeFilter getExposedTypeFilter() {
        return TypeFilterBuilder.getExposedTypeFilter(this);
    }
}

class TypeFilterBuilder {
    static TypeFilter getExposedTypeFilter(ElmExposingBase element) {
        return element.isExposingAll() ? TypeFilter.always(true) : getExposedTypeFilter(element.getExposedUnionList());
    }

    private static TypeFilter getExposedTypeFilter(List<ElmExposedUnion> exposedUnions) {
        Set<String> types = exposedUnions.stream()
                .map(e -> e.getUpperCaseId().getText())
                .collect(Collectors.toSet());
        Set<Pair<String, String>> typeMembers = exposedUnions.stream()
                .flatMap(TypeFilterBuilder::getExposedUnionMembers)
                .collect(Collectors.toSet());
        return TypeFilter.fromSets(types, typeMembers);
    }

    private static Stream<Pair<String, String>> getExposedUnionMembers(ElmExposedUnion element) {
        String unionName = element.getUpperCaseId().getText();
        return Optional.ofNullable(element.getExposedUnionConstructors())
                .map(e -> e.isExposingAll()
                        ? Stream.of(Pair.create(unionName, TypeFilter.ALL_MEMBERS))
                        : getExposedUnionMembers(unionName, e.getUpperCaseIdList()))
                .orElse(Stream.empty());
    }

    private static Stream<Pair<String, String>> getExposedUnionMembers(String unionName, List<ElmUpperCaseId> unionMembers) {
        return unionMembers.stream()
                .map(e -> Pair.create(unionName, e.getText()));
    }
}