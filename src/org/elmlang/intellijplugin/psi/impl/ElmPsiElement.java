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
import java.util.stream.Stream;

public abstract class ElmPsiElement extends ASTWrapperPsiElement {
    public ElmPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Contract(
            pure = true
    )
    public PsiReference[] getReferences() {
        return this.getReferencesList().toArray(PsiReference[]::new);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ElmVisitor) {
            ((ElmVisitor)visitor).visitPsiElement(this);
        }
        else super.accept(visitor);
    }

    public Stream<ElmReference> getReferencesList() {
        return Arrays.stream(this.getChildren())
                .filter(c -> c instanceof ElmPsiElement)
                .map(c -> getReferencesFromChild((ElmPsiElement) c))
                .reduce(Stream.empty(), Stream::concat);
    }

    private Stream<ElmReference> getReferencesFromChild(ElmPsiElement element) {
        return element.getReferencesList()
                .map(r -> r.referenceInAncestor(this));
    }
}
