package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.ElmImportClause;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        PsiElement grandParent = this.referencingElement.getParent().getParent();
        if (!(grandParent instanceof ElmImportClause)) {
            return null;
        }

        return Optional.ofNullable(((ElmImportClause) grandParent).getModuleName())
                .map(PsiElement::getText)
                .map(m -> ElmModuleIndex.getFilesByModuleName(m, this.myElement.getProject()).stream().findFirst())
                .orElse(Optional.empty())
                .map(this::resolve)
                .orElse(null);
    }
}
