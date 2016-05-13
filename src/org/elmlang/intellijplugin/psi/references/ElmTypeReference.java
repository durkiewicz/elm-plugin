package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.scope.ElmScope;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

public class ElmTypeReference extends ElmReferenceBase {
    public ElmTypeReference(ElmUpperCaseId element) {
        super(element);
    }

    private ElmTypeReference(PsiElement element, PsiElement referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, PsiElement, TextRange, ElmReference> constructor() {
        return ElmTypeReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiFile file = this.myElement.getContainingFile();
        return ElmScope.typesFor((ElmFile) file)
                .filter(this::theSameNameOrEmpty)
                .findFirst()
                .map(o -> o.orElse(null))
                .orElse(null);
    }
}
