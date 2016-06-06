package org.elmlang.intellijplugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ElmTypeAnnotationBase extends PsiElement, ElmWithSingleId {
    @NotNull
    ElmTypeDefinition getTypeDefinition();

    default boolean isPortAnnotation() {
        PsiElement element = this.getFirstChild();
        return element instanceof ASTNode && ((ASTNode) element).getElementType().equals(ElmTypes.PORT);
    }
}