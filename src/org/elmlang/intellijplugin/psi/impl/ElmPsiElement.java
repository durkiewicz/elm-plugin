package org.elmlang.intellijplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.elmlang.intellijplugin.psi.references.ElmReferenceImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
                .map(c -> getReferencesFromChild((ElmPsiElement) c))
                .forEach(result::addAll);
        return result;
    }

    private List<PsiReference> getReferencesFromChild(ElmPsiElement element) {
        return element.getReferencesList().stream()
                .map(r -> ((ElmReferenceImpl)r).referenceInAncestor(this))
                .collect(Collectors.toList());
    }
}
