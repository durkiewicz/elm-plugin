package org.elmlang.intellijplugin.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface ElmReference extends PsiReference {
    ElmReferenceImpl referenceInAncestor(PsiElement ancestor);
}
