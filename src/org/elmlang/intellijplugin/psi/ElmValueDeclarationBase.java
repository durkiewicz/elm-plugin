package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElmValueDeclarationBase extends PsiElement {

    @NotNull
    ElmExpression getExpression();

    @Nullable
    ElmFunctionDeclarationLeft getFunctionDeclarationLeft();

    @Nullable
    ElmOperatorDeclarationLeft getOperatorDeclarationLeft();

    @Nullable
    ElmPattern getPattern();
}
