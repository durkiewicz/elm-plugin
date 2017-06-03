package org.elmlang.intellijplugin.features.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.psi.presentation.java.SymbolPresentationUtil
import org.elmlang.intellijplugin.ElmIcons
import org.elmlang.intellijplugin.psi.ElmTypeDeclaration

class ElmUnionTypeTreeElement(element: ElmTypeDeclaration): PsiTreeElementBase<ElmTypeDeclaration>(element) {

    override fun getIcon(open: Boolean) =
            ElmIcons.UNION_TYPE

    override fun getPresentableText(): String? {
        val decl = element
        return when (decl) {
            is ElmTypeDeclaration ->
                decl.upperCaseId.name
            else ->
                decl?.let { SymbolPresentationUtil.getSymbolPresentableText(it) }
        }
    }

    override fun getChildrenBase(): Collection<StructureViewTreeElement> =
            emptyList()
}
