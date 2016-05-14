package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.references.utils.AbsoluteReferencesHelper;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

public class ElmAbsoluteValueReference extends ElmReferenceBase<ElmLowerCaseId> {
    public ElmAbsoluteValueReference(ElmLowerCaseId element) {
        super(element);
    }

    private ElmAbsoluteValueReference(PsiElement element, ElmLowerCaseId referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, ElmLowerCaseId, TextRange, ElmReference> constructor() {
        return ElmAbsoluteValueReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String moduleName = AbsoluteReferencesHelper.getModuleName(
                ((ElmMixedCasePath) this.referencingElement.getParent()).getUpperCaseIdList()
        );
        return this.resolveUsingModuleIndex(
                moduleName,
                f -> f.getExposedValueByName(this.referencingElement.getText())
        );
    }
}
