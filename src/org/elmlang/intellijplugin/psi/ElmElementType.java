package org.elmlang.intellijplugin.psi;

import com.intellij.psi.tree.IElementType;
import org.elmlang.intellijplugin.ElmLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ElmElementType extends IElementType {
    public ElmElementType(@NotNull @NonNls String debugName) {
        super(debugName, ElmLanguage.INSTANCE);
    }
}