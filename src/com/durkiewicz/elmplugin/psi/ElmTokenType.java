package com.durkiewicz.elmplugin.psi;

import com.intellij.psi.tree.IElementType;
import com.durkiewicz.elmplugin.ElmLanguage;
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