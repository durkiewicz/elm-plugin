package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.utils.Function3;
import org.elmlang.intellijplugin.utils.ListUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElmTypeAnnotationReference extends ElmReferenceBase<ElmLowerCaseId> {

    public ElmTypeAnnotationReference(ElmLowerCaseId element) {
        super(element);
    }

    private ElmTypeAnnotationReference(PsiElement element, ElmLowerCaseId referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, ElmLowerCaseId, TextRange, ElmReference> constructor() {
        return ElmTypeAnnotationReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ElmTreeUtil.findFollowingSibling(this.referencingElement.getParent(), e -> e instanceof ElmValueDeclarationBase)
            .map(e -> resolve((ElmValueDeclarationBase) e))
            .orElse(null);
    }

    private PsiElement resolve(ElmValueDeclarationBase declaration) {
        ElmLowerCaseId functionName = Optional.ofNullable(declaration.getFunctionDeclarationLeft())
                .map(ElmFunctionDeclarationLeft::getLowerCaseId)
                .filter(this::theSameName)
                .orElse(null);

        return Optional.ofNullable(declaration.getPattern())
                .map(e -> ListUtils.head(e.getLowerCaseIdList()))
                .orElse(Optional.empty())
                .filter(this::theSameName)
                .orElse(functionName);
    }
}