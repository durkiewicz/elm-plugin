package org.elmlang.intellijplugin.features.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import org.elmlang.intellijplugin.psi.ElmFile
import java.util.stream.Collectors

class ElmFileTreeElement(element: ElmFile): PsiTreeElementBase<ElmFile>(element) {
    override fun getPresentableText(): String? {
        return element?.name
    }

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val file = element ?: return emptyList()

        val valueNodes = file.getValueDeclarations().map { ElmValueDeclarationTreeElement(it) }.collect(Collectors.toList())
        val typeAliasNodes = file.getTypeAliasDeclarations().map { ElmTypeAliasTreeElement(it) }.collect(Collectors.toList())
        val unionTypeNodes = file.getTypeDeclarations().map { ElmUnionTypeTreeElement(it) }.collect(Collectors.toList())

        return listOf(valueNodes, typeAliasNodes, unionTypeNodes)
                .flatten()
                .sortedBy { it.element?.textOffset }
    }
}