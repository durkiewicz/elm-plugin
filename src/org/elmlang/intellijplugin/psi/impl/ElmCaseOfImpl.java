package org.elmlang.intellijplugin.psi.impl;



import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elmlang.intellijplugin.psi.ElmTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elmlang.intellijplugin.psi.*;

public class ElmCaseOfImpl extends ASTWrapperPsiElement implements ElmCaseOf {

    public ElmCaseOfImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) ((ElmVisitor)visitor).visitPsiElement(this);
        else super.accept(visitor);
    }
}
