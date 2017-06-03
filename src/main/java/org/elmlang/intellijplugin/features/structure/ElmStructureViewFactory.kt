package org.elmlang.intellijplugin.features.structure

import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import org.elmlang.intellijplugin.psi.ElmFile

class ElmStructureViewFactory: PsiStructureViewFactory {

    override fun getStructureViewBuilder(psiFile: PsiFile?): TreeBasedStructureViewBuilder {
        val elmFile = psiFile as ElmFile
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?) =
                    ElmStructureViewModel(elmFile)
        }
    }
}