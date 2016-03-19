package org.elmlang.intellijplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class ElmPsiElement extends ASTWrapperPsiElement {
    public ElmPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Contract(
            pure = true
    )
    public PsiReference[] getReferences() {
        List<PsiReference> references = this.getReferencesList();
        return references.toArray(new PsiReference[references.size()]);
    }

    public List<PsiReference> getReferencesList() {
        List<PsiReference> result = new LinkedList<>();
        Arrays.stream(this.getChildren())
                .filter(c -> c instanceof ElmPsiElement)
                .map(c -> ((ElmPsiElement) c).getReferencesList())
                .forEach(result::addAll);
        return result;
    }
}
