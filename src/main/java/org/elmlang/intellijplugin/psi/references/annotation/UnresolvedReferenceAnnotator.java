package org.elmlang.intellijplugin.psi.references.annotation;

import com.intellij.lang.annotation.*;
import com.intellij.psi.*;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.impl.ElmPsiElement;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.psi.references.ElmReferenceTarget;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class UnresolvedReferenceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        if (shouldCheckReferences(psiElement)) {
            checkReferences((ElmPsiElement) psiElement, holder);
        }
        checkRecord(psiElement, holder);
    }

    private static boolean shouldCheckReferences(PsiElement psiElement) {
        return (isPathElement(psiElement) && !(psiElement.getParent() instanceof ElmExposingBase))
                || psiElement instanceof ElmExposingBase;
    }

    private static boolean isPathElement(PsiElement psiElement) {
        return psiElement instanceof ElmLowerCasePath || psiElement instanceof ElmMixedCasePath || psiElement instanceof ElmUpperCasePath;
    }

    private static void checkReferences(ElmPsiElement psiElement, AnnotationHolder holder) {
        psiElement.getReferencesStream()
                .forEach(r -> annotateIfUnresolved(r, holder));
    }

    private static void checkRecord(PsiElement psiElement, AnnotationHolder holder) {
        if (!(psiElement instanceof ElmRecord)) {
            return;
        }
        ElmRecord record = (ElmRecord) psiElement;
        Optional.ofNullable(record.getLowerCaseId())
                .map(x -> record.getReferencesStream())
                .flatMap(Stream::findFirst)
                .ifPresent(r -> annotateIfUnresolved(r, holder));
    }

    private static void annotateIfUnresolved(ElmReference reference, AnnotationHolder holder) {
        if (reference.resolve() == null) {
            ElmReferenceTarget target = reference.getTarget();
            String additionalMessage = target == ElmReferenceTarget.MODULE
                    ? "Make sure you have added proper packages to the elm-package.json file, have run the `elm-package install`, and have NOT marked the `elm-stuff` directory as excluded."
                    : "";
            String message = String.format("Cannot find %s \"%s\".%s",
                    target,
                    reference.getCanonicalText(),
                    additionalMessage
            );
            holder.createErrorAnnotation(reference.getReferencingElement(), message);
        }
    }
}
