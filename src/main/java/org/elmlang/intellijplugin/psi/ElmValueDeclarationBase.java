package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.impl.ElmValueDeclarationMixin;
import org.elmlang.intellijplugin.psi.impl.ValueDeclarationRole;
import org.jetbrains.annotations.Nullable;

public interface ElmValueDeclarationBase extends PsiElement, ElmWithExpression, ElmWithDisplayName {
    @Nullable
    ElmFunctionDeclarationLeft getFunctionDeclarationLeft();

    @Nullable
    ElmOperatorDeclarationLeft getOperatorDeclarationLeft();

    @Nullable
    ElmPattern getPattern();

    ValueDeclarationRole getRole();
}
