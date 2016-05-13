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

public class ElmAbsoluteValueReference extends ElmReferenceBase {
    public ElmAbsoluteValueReference(ElmLowerCaseId element) {
        super(element);
    }

    private ElmAbsoluteValueReference(PsiElement element, PsiElement referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, PsiElement, TextRange, ElmReference> constructor() {
        return ElmAbsoluteValueReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String moduleName = getModuleName((ElmMixedCasePath) this.referencingElement.getParent());
        return ElmModuleIndex.getFilesByModuleName(moduleName, this.myElement.getProject()).stream()
                .map(f -> f.getExposedValueByName(this.referencingElement.getText()))
                .findFirst()
                .orElse(Optional.empty())
                .orElse(null);
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
            return file.getImportClauses().stream()
                    .filter(e -> {
                        ElmAsClause asClause = e.getAsClause();
                        return asClause != null
                                && asClause.getUpperCaseId().getText().equals(upperCaseIdList.get(0).getText());
                    })
                    .findFirst()
                    .map(e -> e.getModuleName().getUpperCaseIdList())
                    .orElse(upperCaseIdList);
        } else {
            return upperCaseIdList;
        }
    }
}
