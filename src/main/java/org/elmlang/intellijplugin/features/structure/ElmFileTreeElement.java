package org.elmlang.intellijplugin.features.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmValueDeclarationBase;
import org.elmlang.intellijplugin.utils.StreamUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

class ElmFileTreeElement extends PsiTreeElementBase<ElmFile> {
    @NotNull
    private ElmFile element;

    ElmFileTreeElement(@NotNull ElmFile psiElement) {
        super(psiElement);
        element = psiElement;
    }

    @Nullable
    public String getPresentableText() {
        return element.getName();
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return StreamUtil.concatAll(
                element.getValueDeclarations().map((ElmValueDeclarationBase psiElement) -> new ElmValueDeclarationTreeElement(psiElement)),
                element.getTypeAliasDeclarations().map(ElmTypeAliasTreeElement::new),
                element.getTypeDeclarations().map(ElmUnionTypeTreeElement::new)
        )
                .filter(a -> a.getElement() != null)
                .sorted(Comparator.comparingInt(a -> a.getElement().getTextOffset()))
                .collect(Collectors.toList());
    }
}