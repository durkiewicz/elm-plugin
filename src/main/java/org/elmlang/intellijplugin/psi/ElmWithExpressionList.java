package org.elmlang.intellijplugin.psi;;

import com.intellij.psi.PsiElement;

import java.util.List;

public interface ElmWithExpressionList extends PsiElement {
    List<ElmExpression> getExpressionList();
}
