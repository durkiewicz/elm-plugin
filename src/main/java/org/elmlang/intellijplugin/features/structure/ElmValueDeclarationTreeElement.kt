package org.elmlang.intellijplugin.features.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import org.elmlang.intellijplugin.ElmIcons
import org.elmlang.intellijplugin.psi.ElmValueDeclarationBase
import javax.swing.Icon


class ElmValueDeclarationTreeElement(element: ElmValueDeclarationBase): PsiTreeElementBase<ElmValueDeclarationBase>(element) {

    override fun getIcon(open: Boolean): Icon? {
        val decl = element ?: return null
        return decl.getIcon(open)
    }

    override fun getPresentableText(): String? {
        val decl = element ?: return ""
        return decl.getName()
    }

    override fun getChildrenBase(): Collection<StructureViewTreeElement> =
            emptyList()
}

private enum class Role {
    VALUE,
    FUNCTION
}

private val ElmValueDeclarationBase.role: Role
    // TODO [kl] where is the right place to put this logic? a mixin and injected method?
    get() {
        val decl = functionDeclarationLeft
        if (decl == null) {
            return Role.VALUE
        }

        return if (decl.patternList.isEmpty())
            Role.VALUE
        else
            Role.FUNCTION
    }

private fun ElmValueDeclarationBase.getIcon(open: Boolean) =
        when (role) {
            Role.VALUE ->
                ElmIcons.VALUE

            Role.FUNCTION ->
                ElmIcons.FUNCTION
        }

private fun ElmValueDeclarationBase.getName() =
        when (role) {
            Role.VALUE ->
                pattern?.lowerCaseIdList?.firstOrNull()?.name ?: "unknown value"

            Role.FUNCTION ->
                functionDeclarationLeft?.lowerCaseId?.name ?: "unknown function"
        }