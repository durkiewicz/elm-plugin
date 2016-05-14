package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.references.utils.AbsoluteReferencesHelper;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElmAbsoluteTypeReference extends ElmReferenceBase<ElmUpperCaseId> {
    private final List<ElmUpperCaseId> moduleName;

    public ElmAbsoluteTypeReference(ElmUpperCaseId element, List<ElmUpperCaseId> moduleName) {
        super(element);
        this.moduleName = moduleName;
    }

    private ElmAbsoluteTypeReference(PsiElement element, ElmUpperCaseId referencingElement, TextRange rangeInElement, List<ElmUpperCaseId> moduleName) {
        super(element, referencingElement, rangeInElement);
        this.moduleName = moduleName;
    }

    @Override
    protected Function3<PsiElement, ElmUpperCaseId, TextRange, ElmReference> constructor() {
        return (element, id, textRange) -> new ElmAbsoluteTypeReference(element, id, textRange, this.moduleName);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String moduleName = AbsoluteReferencesHelper.getModuleName(this.moduleName);
        return this.resolveUsingModuleIndex(
                moduleName,
                f -> f.getExposedType(this.referencingElement.getText())
        );
    }
}
