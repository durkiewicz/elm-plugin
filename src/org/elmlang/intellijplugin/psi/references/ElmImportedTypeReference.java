package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.references.utils.ExposingClauseReferenceHelper;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

public class ElmImportedTypeReference extends ElmExposedTypeReference {
    public ElmImportedTypeReference(ElmUpperCaseId element) {
        super(element);
    }

    private ElmImportedTypeReference(PsiElement element, ElmUpperCaseId referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, ElmUpperCaseId, TextRange, ElmReference> constructor() {
        return ElmImportedTypeReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ExposingClauseReferenceHelper.resolveImported(this.referencingElement, this::resolve);
    }
}
