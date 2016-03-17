package org.elmlang.intellijplugin.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.elmlang.intellijplugin.ElmLanguage;
import org.jetbrains.annotations.NotNull;

public class ElmReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                 @NotNull ProcessingContext context) {
        if (!element.getLanguage().is(ElmLanguage.INSTANCE)) {
            return PsiReference.EMPTY_ARRAY;
        }

        if (element instanceof PsiNamedElement) {
            PsiNamedElement se = (PsiNamedElement) element;
            return new PsiReference[]{new ElmReference(se, se.getTextRange())};
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
