package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.util.Function;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.utils.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElmReference extends PsiReferenceBase<PsiElement> {
    private final Function<ElmLowerCaseId, Boolean> sameName;

    public ElmReference(PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
        sameName = new Function<ElmLowerCaseId, Boolean>() {
            @Override
            public Boolean fun(ElmLowerCaseId element) {
                return  ElmReference.this.myElement.getText().equals(element.getText());
            }
        };
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (isSimpleValueReference(this.myElement)) {
            List<ElmLowerCaseId> closureDeclarations = ElmTreeUtil.getClosureDeclarations(this.myElement);
            return ListUtils.find(closureDeclarations, sameName);
        }
        return null;
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
