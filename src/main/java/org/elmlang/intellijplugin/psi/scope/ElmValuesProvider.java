package org.elmlang.intellijplugin.psi.scope;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.impl.ElmPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Predicate;

class ElmValuesProvider
{
    private PsiElement elem;
    private Stack<ElmPattern> patterns = new Stack<>();
    private Stack<ElmLowerCaseId> ids = new Stack<>();

    ElmValuesProvider(PsiElement elem) {
        this.elem = elem;
    }

    Optional<ElmLowerCaseId> nextId() {
        if (!this.ids.isEmpty()) {
            return Optional.of(ids.pop());
        }

        if (!this.patterns.isEmpty()) {
            ElmPsiImplUtil.getDeclarationsFromPattern(this.patterns.pop())
                    .forEach(ids::add);
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
        this.gatherValueDeclarations((ElmWithValueDeclarations) this.elem);
    }

    private void gatherValueDeclarations(ElmWithValueDeclarations element) {
        Arrays.stream(element.getChildren())
                .filter(c -> c instanceof ElmValueDeclarationBase)
                .forEach(d -> {
                    PsiElement child = d.getFirstChild();
                    if (child instanceof ElmPattern) {
                        this.patterns.add((ElmPattern) child);
                    } else if (child instanceof ElmFunctionDeclarationLeft) {
                        this.ids.push(((ElmFunctionDeclarationLeft) child).getLowerCaseId());
                    } else if (d instanceof ElmValueDeclaration) {
                        Optional.ofNullable(((ElmValueDeclaration) d).getPortDeclarationLeft())
                                .ifPresent(e -> this.ids.push(e.getLowerCaseId()));
                    }
                });
        Arrays.stream(element.getChildren())
                .filter(e -> e instanceof ElmTypeAnnotation)
                .map(e -> (ElmTypeAnnotation) e)
                .forEach(typeAnnotation -> {
                    PsiElement child = typeAnnotation.getFirstChild();
                    if (startsWithPort(child)) {
                        Optional.ofNullable(typeAnnotation.getLowerCaseId())
                                .ifPresent(id -> {
                                    boolean isFollowedByPortDefinition = ElmTreeUtil.findFollowingSibling(typeAnnotation, e -> e instanceof ElmValueDeclaration)
                                            .flatMap(e -> Optional.ofNullable(((ElmValueDeclaration) e).getPortDeclarationLeft()))
                                            .filter(e -> e.getLowerCaseId().getText().equals(id.getText())).isPresent();
                                    if (!isFollowedByPortDefinition) {
                                        this.ids.push(id);
                                    }
                                });
                    }
                });
    }

    private static boolean startsWithPort(PsiElement element) {
        return element instanceof ASTNode && ((ASTNode) element).getElementType().equals(ElmTypes.PORT);
    }

    private void gatherDeclarationsFromOtherFiles() {
        ((ElmFile) this.elem).getImportClauses().stream()
                .filter(e -> e.getExposingClause() != null)
                .forEach(this::gatherDeclarationsFromOtherFile);
        gatherDeclarationsFromOtherFile(ElmCoreLibrary.BASICS_MODULE, x -> true);
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
