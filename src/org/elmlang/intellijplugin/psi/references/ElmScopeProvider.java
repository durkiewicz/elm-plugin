package org.elmlang.intellijplugin.psi.references;


import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class ElmScopeProvider {
    PsiElement elem;
    Stack<ElmPattern> patterns = new Stack<>();
    Stack<ElmLowerCaseId> ids = new Stack<>();

    private ElmScopeProvider(PsiElement elem) {
        this.elem = elem;
    }

    private Optional<ElmLowerCaseId> nextId() {
        if (!this.ids.isEmpty()) {
            return Optional.of(ids.pop());
        }

        if (!this.patterns.isEmpty()) {
            ids.addAll(getDeclarationsFromPattern(this.patterns.pop()));
            return nextId();
        }

        if (this.elem == null || this.elem instanceof ElmPattern) {
            return Optional.empty();
        }

        Arrays.stream(this.elem.getChildren())
                .filter(c -> c instanceof ElmPattern)
                .map(p -> (ElmPattern)p)
                .forEach(this.patterns::add);

        if (this.elem instanceof ElmValueDeclarationBase) {
            ElmValueDeclarationBase declaration = (ElmValueDeclarationBase) this.elem;
            Optional.ofNullable(declaration.getFunctionDeclarationLeft())
                    .ifPresent(this::gatherDeclarations);
            Optional.ofNullable(declaration.getOperatorDeclarationLeft())
                    .ifPresent(this::gatherDeclarations);
        }

        this.elem = this.elem.getParent();

        return nextId();
    }

    public static Stream<Optional<ElmLowerCaseId>> scopeFor(ElmLowerCaseId elem) {
        ElmScopeProvider p = new ElmScopeProvider(elem.getParent());
        return Stream.generate(p::nextId);
    }

    private void gatherDeclarations(ElmFunctionDeclarationLeft elem) {
        this.ids.push(elem.getLowerCaseId());
        this.patterns.addAll(elem.getPatternList());
    }

    private void gatherDeclarations(ElmWithPatternList elem) {
        this.patterns.addAll(elem.getPatternList());
    }

    private static List<ElmLowerCaseId> getDeclarationsFromPattern(ElmPattern pattern) {
        if (pattern == null) {
            return Collections.emptyList();
        }

        List<ElmLowerCaseId> result = new LinkedList<>();

        result.addAll(pattern.getLowerCaseIdList());

        addDeclarationsToResult(
                result,
                pattern.getListPatternList(),
                ElmScopeProvider::getDeclarationsFromParentPattern);

        addDeclarationsToResult(
                result,
                pattern.getParenthesedPatternList(),
                p -> getDeclarationsFromPattern(p.getPattern()));

        addDeclarationsToResult(
                result,
                pattern.getRecordPatternList(),
                ElmRecordPattern::getLowerCaseIdList);

        addDeclarationsToResult(
                result,
                pattern.getTuplePatternList(),
                ElmScopeProvider::getDeclarationsFromParentPattern);

        addDeclarationsToResult(
                result,
                pattern.getUnionPatternList(),
                ElmScopeProvider::getDeclarationsFromParentPattern);

        return result;
    }

    private static <T> void addDeclarationsToResult(List<ElmLowerCaseId> result, List<T> source, Function<T, List<ElmLowerCaseId>> f) {
        source.stream()
                .map(f)
                .forEach(result::addAll);
    }

    private static <T extends ElmWithPatternList> List<ElmLowerCaseId> getDeclarationsFromParentPattern(ElmWithPatternList parentPattern) {
        List<ElmLowerCaseId> result = new LinkedList<>();
        parentPattern.getPatternList().stream()
                .map(ElmScopeProvider::getDeclarationsFromPattern)
                .forEach(result::addAll);
        return result;
    }
}
