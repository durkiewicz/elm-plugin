package org.intellij.elmlang.elmplugin.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.elmlang.elmplugin.ElmLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ElmElementType extends IElementType {
    public ElmElementType(@NotNull @NonNls String debugName) {
        super(debugName, ElmLanguage.INSTANCE);
    }
}