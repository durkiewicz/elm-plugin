package org.intellij.elmlang.elmplugin.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.elmlang.elmplugin.ElmLanguage;
import org.intellij.elmlang.elmplugin.ElmLanguage;
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