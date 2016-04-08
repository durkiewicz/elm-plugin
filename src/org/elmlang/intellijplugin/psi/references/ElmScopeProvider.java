package org.elmlang.intellijplugin.psi.references;


import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.impl.ElmPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ElmScopeProvider {
    private static final String BASICS_MODULE = "Basics";

    PsiElement elem;
    Stack<ElmPattern> patterns = new Stack<>();
    Stack<ElmLowerCaseId> ids = new Stack<>();

    private ElmScopeProvider(PsiElement elem) {
        this.elem = elem;
    }

    public static Stream<Optional<ElmLowerCaseId>> scopeFor(ElmLowerCaseId elem) {
        ElmScopeProvider p = new ElmScopeProvider(elem.getParent());
        return Stream.generate(p::nextId);
    }

    private Optional<ElmLowerCaseId> nextId() {
        if (!this.ids.isEmpty()) {
            return Optional.of(ids.pop());
        }

        if (!this.patterns.isEmpty()) {
            ids.addAll(ElmPsiImplUtil.getDeclarationsFromPattern(this.patterns.pop()));
            return nextId();
        }

        if (this.elem == null || this.elem instanceof ElmPattern) {
            return Optional.empty();
        }

        this.elem = this.gatherIdsFromCurrentElement();

        return nextId();
    }

    private PsiElement gatherIdsFromCurrentElement() {
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
        } else if (this.elem instanceof ElmLetIn) {
            this.gatherValueDeclarations();
        } else if (this.elem instanceof ElmFile) {
            this.gatherValueDeclarations();
            this.gatherDeclarationsFromOtherFiles();
            return null;
        }

        return this.elem.getParent();
    }

    private void gatherDeclarations(ElmFunctionDeclarationLeft elem) {
        this.ids.push(elem.getLowerCaseId());
        this.patterns.addAll(elem.getPatternList());
    }

    private void gatherValueDeclarations() {
        this.gatherValueDeclarations((ElmWithValueDeclarations) this.elem, x -> true);
    }

    private void gatherValueDeclarations(ElmWithValueDeclarations element, Predicate<? super PsiElement> additionalPredicate) {
        Arrays.stream(element.getChildren())
                .filter(c -> c instanceof ElmValueDeclarationBase && additionalPredicate.test(c))
                .forEach(d -> {
                    PsiElement child = d.getFirstChild();
                    if (child instanceof ElmPattern) {
                        this.patterns.add((ElmPattern) child);
                    } else if (child instanceof ElmFunctionDeclarationLeft) {
                        this.ids.push(((ElmFunctionDeclarationLeft) child).getLowerCaseId());
                    }
                });
    }

    private void gatherDeclarationsFromOtherFiles() {
        ((ElmFile) this.elem).getImportClauses().stream()
                .filter(e -> e.getExposingClause() != null)
                .forEach(this::gatherDeclarationsFromOtherFile);
        gatherDeclarationsFromOtherFile(BASICS_MODULE, x -> true);
    }

    private void gatherDeclarationsFromOtherFile(@NotNull ElmImportClause elem) {
        Predicate<ElmLowerCaseId> filter = Optional.ofNullable(elem.getExposingClause())
                .map(ElmExposingBase::getLowerCaseFilter)
                .orElse(x -> false);
        Optional.ofNullable(elem.getModuleName())
                .ifPresent(name -> gatherDeclarationsFromOtherFile(name.getText(), filter));
    }

    private void gatherDeclarationsFromOtherFile(@NotNull String moduleName, Predicate<ElmLowerCaseId> filter) {
        ElmModuleIndex.getFilesByModuleName(moduleName, this.elem.getProject()).stream()
                .forEach(f -> gatherDeclarationsFromOtherFile(f, filter));
    }

    private void gatherDeclarationsFromOtherFile(ElmFile file, Predicate<ElmLowerCaseId> filter) {
        file.getExposedValues()
                .filter(filter)
                .forEach(this.ids::add);
    }

    private void gatherDeclarations(ElmWithPatternList elem) {
        this.patterns.addAll(elem.getPatternList());
    }


}
