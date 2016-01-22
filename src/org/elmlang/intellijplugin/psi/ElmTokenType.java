package org.elmlang.intellijplugin.psi;

import com.intellij.psi.tree.IElementType;
import org.elmlang.intellijplugin.ElmLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ElmTokenType extends IElementType {
    public ElmTokenType(@NotNull @NonNls String debugName) {
        super(debugName, ElmLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "ElmTokenType." + super.toString();
    }
}