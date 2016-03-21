package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.ElmVisitor;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.psi.references.ElmReferenceImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public abstract class ElmPathBase extends ElmPsiElement {
    public ElmPathBase(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) {
            ((ElmVisitor)visitor).visitPsiElement(this);
        }
        else super.accept(visitor);
    }

    public List<ElmReference> getReferencesList() {
        return Arrays.stream(this.getChildren())
                .filter(e -> e instanceof ElmLowerCaseId || e instanceof ElmUpperCaseId)
                .map(child -> new ElmReferenceImpl(child).referenceInAncestor(this))
                .collect(Collectors.toList());
    }
}