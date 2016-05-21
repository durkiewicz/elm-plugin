package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.references.utils.ExposingClauseReferenceHelper;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

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
        return ExposingClauseReferenceHelper.resolveExposed(this.referencingElement, this::resolve);
    }

    PsiElement resolve(ElmFile file) {
        return file.getExposedValues()
                .filter(this::theSameName)
                .findFirst()
                .orElse(null);
    }
}
