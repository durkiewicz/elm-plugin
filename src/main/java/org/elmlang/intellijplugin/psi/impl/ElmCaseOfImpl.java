package org.elmlang.intellijplugin.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elmlang.intellijplugin.psi.*;

public class ElmCaseOfImpl extends ElmPsiElement implements ElmCaseOf {

    public ElmCaseOfImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) {
            ((ElmVisitor)visitor).visitPsiElement(this);
        }
        else super.accept(visitor);
    }
}
