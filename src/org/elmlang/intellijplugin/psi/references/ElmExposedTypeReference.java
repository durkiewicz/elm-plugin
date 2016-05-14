package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.references.utils.ExposingClauseReferenceHelper;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

public class ElmExposedTypeReference extends ElmReferenceBase<ElmUpperCaseId> {
    public ElmExposedTypeReference(ElmUpperCaseId element) {
        super(element);
    }

    ElmExposedTypeReference(PsiElement element, ElmUpperCaseId referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, ElmUpperCaseId, TextRange, ElmReference> constructor() {
        return ElmExposedTypeReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ExposingClauseReferenceHelper.resolveExposed(this.referencingElement, this::resolve);
    }

    PsiElement resolve(ElmFile file) {
        return file.getExposedType(this.referencingElement.getText())
                .orElse(null);
    }
}
