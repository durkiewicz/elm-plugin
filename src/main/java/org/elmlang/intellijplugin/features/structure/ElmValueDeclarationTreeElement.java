package org.elmlang.intellijplugin.features.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.elmlang.intellijplugin.ElmIcons;
import org.elmlang.intellijplugin.psi.ElmValueDeclarationBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ElmValueDeclarationTreeElement extends PsiTreeElementBase<ElmValueDeclarationBase> {
    ElmValueDeclarationTreeElement(ElmValueDeclarationBase psiElement) {
        super(psiElement);
    }

    @Override
    public Icon getIcon(boolean open) {
        return Optional.ofNullable(this.getElement())
                .map(e -> {
                    switch (e.getRole()) {
                        case VALUE: return ElmIcons.VALUE;
                        case FUNCTION: return ElmIcons.FUNCTION;
                        case OPERATOR: return ElmIcons.FUNCTION;
                        default: return null;
                    }
                }).orElse(null);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return Optional.ofNullable(this.getElement())
                .map(ElmValueDeclarationBase::getDisplayName)
                .orElse(null);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }
}
