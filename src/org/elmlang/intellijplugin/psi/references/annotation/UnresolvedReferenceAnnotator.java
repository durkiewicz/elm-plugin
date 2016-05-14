package org.elmlang.intellijplugin.psi.references.annotation;

import com.intellij.lang.annotation.*;
import com.intellij.psi.*;
import org.elmlang.intellijplugin.psi.ElmLowerCasePath;
import org.elmlang.intellijplugin.psi.ElmMixedCasePath;
import org.elmlang.intellijplugin.psi.ElmUpperCasePath;
import org.elmlang.intellijplugin.psi.impl.ElmPsiElement;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.jetbrains.annotations.NotNull;

public class UnresolvedReferenceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        if (psiElement instanceof ElmLowerCasePath || psiElement instanceof ElmMixedCasePath || psiElement instanceof ElmUpperCasePath) {
            checkReferences((ElmPsiElement) psiElement, holder);
        }
    }

    private static void checkReferences(ElmPsiElement psiElement, AnnotationHolder holder) {
        psiElement.getReferencesStream()
                .forEach(r -> annotateIfUnresolved(r, holder));
    }

    private static void annotateIfUnresolved(ElmReference reference, AnnotationHolder holder) {
        if (reference.resolve() == null) {
            holder.createErrorAnnotation(reference.getReferencingElement(), String.format("Cannot find %s \"%s\"", reference.getTargetName(), reference.getCanonicalText()));
        }
    }
}
