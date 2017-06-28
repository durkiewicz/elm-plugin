package org.elmlang.intellijplugin.features.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.utils.StreamUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class ElmFileTreeElement extends PsiTreeElementBase<ElmFile> {
    private ElmFile element;

    ElmFileTreeElement(ElmFile psiElement) {
        super(psiElement);
        element = psiElement;
    }

    @Nullable
    public String getPresentableText() {
        return element == null ? null : element.getName();
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        if (element == null) {
            return Collections.emptyList();
        }

        return StreamUtil.concatAll(
                element.getValueDeclarations().map(ElmValueDeclarationTreeElement::new),
                element.getTypeAliasDeclarations().map(ElmTypeAliasTreeElement::new),
                element.getTypeDeclarations().map(ElmUnionTypeTreeElement::new))
                .filter(a -> a.getElement() != null)
                .sorted(Comparator.comparingInt(a -> a.getElement().getTextOffset()))
                .collect(Collectors.toCollection((Supplier<Collection<StructureViewTreeElement>>) ArrayList::new));
    }
}