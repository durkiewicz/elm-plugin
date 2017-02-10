package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmMixedCasePath;
import org.elmlang.intellijplugin.psi.ElmUpperCasePath;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElmContainingModuleReference extends ElmReferenceBase<PsiElement> {
    private final int modulePartLength;
    private final ElmReference valueOrTypeReference;

    public ElmContainingModuleReference(ElmUpperCasePath element, TextRange textRange, int modulePartLength, ElmReference valueOrTypeReference) {
        this(element, element, textRange, modulePartLength, valueOrTypeReference);
    }

    public ElmContainingModuleReference(ElmMixedCasePath element, TextRange textRange, int modulePartLength, ElmReference valueOrTypeReference) {
        this(element, element, textRange, modulePartLength, valueOrTypeReference);
    }

    private ElmContainingModuleReference(
            PsiElement element,
            PsiElement referencingElement,
            TextRange rangeInElement,
            int modulePartLength,
            ElmReference valueOrTypeReference) {
        super(element, referencingElement, rangeInElement);
        this.modulePartLength = modulePartLength;
        this.valueOrTypeReference = valueOrTypeReference;
    }

    @Override
    protected Function3<PsiElement, PsiElement, TextRange, ElmReference> constructor() {
        return (element, path, textRange) ->
                new ElmContainingModuleReference(
                        element,
                        path,
                        textRange,
                        this.modulePartLength,
                        this.valueOrTypeReference);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return Optional.ofNullable(valueOrTypeReference.resolve())
                .map(e -> ((ElmFile)e.getContainingFile()))
                .flatMap(ElmFile::getModuleDeclaration)
                .orElse(null);
    }
}
