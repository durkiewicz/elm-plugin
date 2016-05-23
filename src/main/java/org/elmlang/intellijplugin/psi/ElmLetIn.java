package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElmLetIn extends PsiElement, ElmWithValueDeclarations {
    @NotNull
    List<ElmInnerValueDeclaration> getInnerValuesList();
}
