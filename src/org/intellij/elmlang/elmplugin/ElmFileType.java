package org.intellij.elmlang.elmplugin;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ElmFileType extends LanguageFileType {
    public static final ElmFileType INSTANCE = new ElmFileType();

    private ElmFileType() {
        super(ElmLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Elm file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Elm language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "elm";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ElmIcons.FILE;
    }
}