package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import org.elmlang.intellijplugin.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElmReference extends PsiReferenceBase<PsiElement> {

    public ElmReference(PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (isSimpleValueReference(this.myElement)) {
            return ElmScopeProvider.scopeFor((ElmLowerCaseId)this.myElement)
                    .filter(this::theSameNameOrEmpty)
                    .findFirst()
                    .map(o -> o.orElse(null))
                    .orElse(null);
        }
        return null;
    }

    private boolean theSameNameOrEmpty(Optional<ElmLowerCaseId> optionalId) {
        return optionalId.map(id -> this.myElement.getText().equals(id.getText()))
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
