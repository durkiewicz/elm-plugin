package org.elmlang.intellijplugin.features.structure

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import org.elmlang.intellijplugin.psi.ElmFile

class ElmStructureViewModel(elmFile: ElmFile):
        StructureViewModelBase(elmFile, ElmFileTreeElement(elmFile)),
        StructureViewModel.ElementInfoProvider {

    override fun getSorters() =
            arrayOf(Sorter.ALPHA_SORTER)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?)
            = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement?)
            = element is ElmFile
}