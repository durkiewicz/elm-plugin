package org.elmlang.intellijplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elmlang.intellijplugin.psi.ElmMixedCasePath;
import org.elmlang.intellijplugin.psi.ElmVisitor;
import org.jetbrains.annotations.NotNull;

public class ElmMixedCasePathImpl extends ASTWrapperPsiElement implements ElmMixedCasePath {
    public ElmMixedCasePathImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) {
            ((ElmVisitor)visitor).visitPsiElement(this);
        }
        else super.accept(visitor);
    }
}
