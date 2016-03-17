package org.elmlang.intellijplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.elmlang.intellijplugin.psi.ElmNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class ElmNamedElementImpl extends ASTWrapperPsiElement implements ElmNamedElement {
    public ElmNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}