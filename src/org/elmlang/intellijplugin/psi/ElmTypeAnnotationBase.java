package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ElmTypeAnnotationBase extends PsiElement, ElmWithSingleId {
    @NotNull
    ElmTypeReference getTypeReference();
}