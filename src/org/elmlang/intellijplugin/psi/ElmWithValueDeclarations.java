package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public interface ElmWithValueDeclarations extends PsiElement {
    @NotNull
    Stream<ElmValueDeclarationBase> getValueDeclarations();
}
