package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.references.utils.ExposingClauseReferenceHelper;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

public class ElmImportedValueReference extends ElmExposedValueReference {
    public ElmImportedValueReference(ElmLowerCaseId element) {
        super(element);
    }

    private ElmImportedValueReference(PsiElement element, ElmLowerCaseId referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, ElmLowerCaseId, TextRange, ElmReference> constructor() {
        return ElmImportedValueReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ExposingClauseReferenceHelper.resolveImported(this.referencingElement, this::resolve);
    }
}
