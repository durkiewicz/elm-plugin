package org.elmlang.intellijplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elmlang.intellijplugin.psi.ElmFieldAccess;
import org.elmlang.intellijplugin.psi.ElmVisitor;
import org.jetbrains.annotations.NotNull;

public class ElmFieldAccessImpl extends ASTWrapperPsiElement implements ElmFieldAccess {
    public ElmFieldAccessImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) {
            ((ElmVisitor)visitor).visitPsiElement(this);
        }
        else super.accept(visitor);
    }
}
