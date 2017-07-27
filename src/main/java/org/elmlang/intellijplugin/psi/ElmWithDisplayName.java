package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;

public interface ElmWithDisplayName extends PsiElement {
    String getDisplayName();
}
