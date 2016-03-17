package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;

public interface ElmWithExpression extends PsiElement {
    ElmExpression getExpression();
}
