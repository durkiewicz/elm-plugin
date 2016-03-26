package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ElmWithSingleId extends PsiElement {
    @NotNull
    ElmLowerCaseId getLowerCaseId();
}
