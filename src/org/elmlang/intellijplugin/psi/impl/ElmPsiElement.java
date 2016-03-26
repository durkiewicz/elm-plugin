package org.elmlang.intellijplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import org.elmlang.intellijplugin.psi.ElmVisitor;
import org.elmlang.intellijplugin.psi.references.ElmReference;
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
        List<? extends ElmReference> references = this.getReferencesList();
        return references.toArray(new PsiReference[references.size()]);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) {
            ((ElmVisitor)visitor).visitPsiElement(this);
        }
        else super.accept(visitor);
    }

    public List<? extends ElmReference> getReferencesList() {
        List<ElmReference> result = new LinkedList<>();
        Arrays.stream(this.getChildren())
                .filter(c -> c instanceof ElmPsiElement)
                .map(c -> getReferencesFromChild((ElmPsiElement) c))
                .forEach(result::addAll);
        return result;
    }

    private List<? extends ElmReference> getReferencesFromChild(ElmPsiElement element) {
        return element.getReferencesList().stream()
                .map(r -> r.referenceInAncestor(this))
                .collect(Collectors.toList());
    }
}
