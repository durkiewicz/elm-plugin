package org.elmlang.intellijplugin.features.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.elmlang.intellijplugin.ElmIcons;
import org.elmlang.intellijplugin.psi.ElmTypeDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

class ElmUnionTypeTreeElement extends PsiTreeElementBase<ElmTypeDeclaration> {

    ElmUnionTypeTreeElement(ElmTypeDeclaration psiElement) {
        super(psiElement);
    }

    @Override
    public Icon getIcon(boolean open) {
        return ElmIcons.UNION_TYPE;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return Optional.ofNullable(getElement())
                .map(e -> e.getUpperCaseId().getName())
                .orElse(null);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }
}