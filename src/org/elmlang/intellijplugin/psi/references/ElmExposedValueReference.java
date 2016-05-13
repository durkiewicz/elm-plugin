package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElmExposedValueReference extends ElmReferenceBase<ElmLowerCaseId> {

    public ElmExposedValueReference(ElmLowerCaseId element) {
        super(element);
    }

    ElmExposedValueReference(PsiElement element, ElmLowerCaseId referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, ElmLowerCaseId, TextRange, ElmReference> constructor() {
        return ElmExposedValueReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiFile file;
        try {
            file = this.referencingElement.getContainingFile();
        } catch (PsiInvalidElementAccessException ex) {
            return null;
        }
        return resolve(file);
    }

    private PsiElement resolve(PsiFile file) {
        return Optional.ofNullable(file)
                .filter(f -> f instanceof ElmFile)
                .map(f -> this.resolve((ElmFile)f))
                .orElse(null);
    }

    PsiElement resolve(ElmFile file) {
        return file.getExposedValues()
                .filter(this::theSameName)
                .findFirst()
                .orElse(null);
    }
}
