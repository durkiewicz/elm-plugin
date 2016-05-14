package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.ElmImportClause;
import org.elmlang.intellijplugin.psi.ElmModuleDeclaration;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.ElmUpperCasePath;
import org.elmlang.intellijplugin.psi.references.*;
import org.elmlang.intellijplugin.psi.scope.BuiltInSymbols;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElmUpperCasePathImpl extends ElmPsiElement implements ElmUpperCasePath {
    public ElmUpperCasePathImpl(ASTNode node) {
        super(node);
    }

    public List<ElmUpperCaseId> getUpperCaseIdList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ElmUpperCaseId.class);
    }

    public Stream<ElmReference> getReferencesStream() {
        PsiElement parent = this.getParent();
        if (parent instanceof ElmImportClause) {
            return this.getReferencesInImport();
        } else if (parent instanceof ElmModuleDeclaration) {
            return Stream.empty();
        }

        List<ElmUpperCaseId> children = Arrays.stream(this.getChildren())
                .filter(e -> e instanceof ElmUpperCaseId)
                .map(e -> (ElmUpperCaseId) e)
                .collect(Collectors.toList());
        int size = children.size();
        if (size == 1) {
            return getReferencesFromSingleId(children.get(0));
        } else if (size >= 2) {
            return getReferencesFromNonSinglePath(children);
        }

        return Stream.empty();
    }

    private Stream<ElmReference> getReferencesInImport() {
        return this.getText().startsWith("Native.")
                ? Stream.empty()
                : Stream.of(new ElmFullPathModuleReference(this));
    }

    private static Stream<ElmReference> getReferencesFromSingleId(ElmUpperCaseId element) {
        if (BuiltInSymbols.isBuiltIn(element.getText())) {
            return Stream.empty();
        } else {
            return Stream.of(new ElmTypeReference(element));
        }
    }

    private Stream<ElmReference> getReferencesFromNonSinglePath(List<ElmUpperCaseId> children) {
        ElmUpperCaseId lastChild = children.get(children.size() - 1);
        children.remove(children.size() - 1);
        int moduleTextLength = this.getTextLength() - lastChild.getTextLength() - 1;
        if (moduleTextLength < 0) {
            moduleTextLength = 0;
        }

        return Stream.concat(
                Stream.of(new ElmPartOfPathModuleReference(this, new TextRange(0, moduleTextLength), children.size())),
                Stream.of(new ElmAbsoluteTypeReference(lastChild, children).referenceInAncestor(this))
        );
    }
}
