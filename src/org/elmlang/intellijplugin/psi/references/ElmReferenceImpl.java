package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.elmlang.intellijplugin.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElmReferenceImpl extends PsiReferenceBase<PsiElement> implements ElmReference {
    private final PsiElement referencingElement;

    public ElmReferenceImpl(PsiElement element) {
        this(element, element, new TextRange(0, element.getText().length()));
    }

    private ElmReferenceImpl(PsiElement element, PsiElement referencingElement, TextRange rangeInElement) {
        super(element, rangeInElement);
        this.referencingElement = referencingElement;
    }

    public ElmReferenceImpl referenceInAncestor(PsiElement ancestor) {
        int diff = this.myElement.getTextOffset() - ancestor.getTextOffset();
        TextRange range = this.getRangeInElement();
        return new ElmReferenceImpl(ancestor, this.referencingElement, new TextRange(range.getStartOffset() + diff, range.getEndOffset() + diff));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (isSimpleValueReference(this.referencingElement)) {
            return ElmScopeProvider.scopeFor((ElmLowerCaseId)this.referencingElement)
                    .filter(this::theSameNameOrEmpty)
                    .findFirst()
                    .map(o -> o.orElse(null))
                    .orElse(null);
        }
        return null;
    }

    private boolean theSameNameOrEmpty(Optional<ElmLowerCaseId> optionalId) {
        return optionalId.map(id -> this.referencingElement.getText().equals(id.getText()))
                .orElse(true);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private static boolean isSimpleValueReference(PsiElement elem) {
        return elem instanceof ElmLowerCaseId
                && elem.getParent() instanceof ElmLowerCasePath
                && elem.getParent().getFirstChild() == elem;
    }
}
