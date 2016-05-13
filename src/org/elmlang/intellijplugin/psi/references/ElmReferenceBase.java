package org.elmlang.intellijplugin.psi.references;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

abstract class ElmReferenceBase extends PsiReferenceBase<PsiElement> implements ElmReference {
    final PsiElement referencingElement;

    ElmReferenceBase(PsiElement element) {
        this(element, element, new TextRange(0, element.getText().length()));
    }

    ElmReferenceBase(PsiElement element, PsiElement referencingElement, TextRange rangeInElement) {
        super(element, rangeInElement);
        this.referencingElement = referencingElement;

    }

    public ElmReference referenceInAncestor(PsiElement ancestor) {
        int diff = this.myElement.getTextOffset() - ancestor.getTextOffset();
        TextRange range = this.getRangeInElement();
        return constructor().apply(ancestor, this.referencingElement, new TextRange(range.getStartOffset() + diff, range.getEndOffset() + diff));
    }

    protected abstract Function3<PsiElement, PsiElement, TextRange, ElmReference> constructor();

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    boolean theSameName(@NotNull PsiElement element) {
        return element.getText().equals(this.referencingElement.getText());
    }

    <T extends PsiElement> boolean theSameNameOrEmpty(Optional<T> optionalElem) {
        return optionalElem.map(this::theSameName)
                .orElse(true);
    }
}
