package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.ElmMixedCasePath;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.references.ElmAbsoluteValueReference;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.psi.references.ElmTypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ElmMixedCasePathImpl extends ElmPsiElement implements ElmMixedCasePath {
    public ElmMixedCasePathImpl(ASTNode node) {
        super(node);
    }

    public Stream<ElmReference> getReferencesStream() {
        PsiElement[] children = this.getChildren();

        if (children.length == 1 && children[0] instanceof ElmUpperCaseId) {
            return Stream.of(new ElmTypeReference((ElmUpperCaseId)children[0]));
        }

        return Arrays.stream(this.getChildren())
                .filter(e -> e instanceof ElmLowerCaseId)
                .map(child -> new ElmAbsoluteValueReference((ElmLowerCaseId)child).referenceInAncestor(this))
                .limit(1);
    }

    public List<ElmUpperCaseId> getUpperCaseIdList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ElmUpperCaseId.class);
    }
}
