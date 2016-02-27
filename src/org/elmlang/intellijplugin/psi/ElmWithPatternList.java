package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;

import java.util.List;

public interface ElmWithPatternList extends PsiElement {
    List<ElmPattern> getPatternList();
}
