package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        String moduleName = getModuleName((ElmMixedCasePath) this.referencingElement.getParent());
        return this.resolveUsingModuleIndex(
                moduleName,
                f -> f.getExposedValueByName(this.referencingElement.getText())
        );
    }

    private String getModuleName(ElmMixedCasePath path) {
        return getModuleName(getModuleNameAsList(path));
    }

    private String getModuleName(List<ElmUpperCaseId> path) {
        return path.stream()
                .map(PsiElement::getText)
                .collect(Collectors.joining("."));
    }

    private List<ElmUpperCaseId> getModuleNameAsList(ElmMixedCasePath path) {
        List<ElmUpperCaseId> upperCaseIdList = path.getUpperCaseIdList();
        if (upperCaseIdList.size() == 1) {
            ElmFile file = (ElmFile) path.getContainingFile();
            return file.getImportClauseByAlias(upperCaseIdList.get(0).getText())
                    .map(e -> e.getModuleName().getUpperCaseIdList())
                    .orElse(upperCaseIdList);
        } else {
            return upperCaseIdList;
        }
    }
}
