package org.intellij.elmlang.elmplugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.intellij.elmlang.elmplugin.ElmFileType;
import org.intellij.elmlang.elmplugin.ElmLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ElmFile extends PsiFileBase {
    public ElmFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ElmLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ElmFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Elm File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}