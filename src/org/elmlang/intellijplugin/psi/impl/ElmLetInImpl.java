package org.elmlang.intellijplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.ElmInnerValueDeclaration;
import org.elmlang.intellijplugin.psi.ElmLetIn;
import org.elmlang.intellijplugin.psi.ElmPattern;
import org.elmlang.intellijplugin.psi.ElmVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ElmLetInImpl extends ASTWrapperPsiElement implements ElmLetIn {
    public ElmLetInImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) {
            ((ElmVisitor)visitor).visitPsiElement(this);
        }
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public List<ElmInnerValueDeclaration> getInnerValuesList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ElmInnerValueDeclaration.class);
    }
}
