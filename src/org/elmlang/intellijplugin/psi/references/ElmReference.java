package org.elmlang.intellijplugin.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface ElmReference extends PsiReference {
    ElmReference referenceInAncestor(PsiElement ancestor);

    PsiElement getReferencingElement();

    ElmReferenceTarget getTarget();
}
