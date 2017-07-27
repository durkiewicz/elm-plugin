package org.elmlang.intellijplugin.features.structure;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.jetbrains.annotations.NotNull;

public class ElmStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {

    ElmStructureViewModel(@NotNull ElmFile elmFile) {
        super(elmFile, new ElmFileTreeElement(elmFile));
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return new Sorter[] {Sorter.ALPHA_SORTER};
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof ElmFile;
    }
}
