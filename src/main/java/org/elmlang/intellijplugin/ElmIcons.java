package org.elmlang.intellijplugin;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class ElmIcons {
    private static final String ELM_ICONS_PATH = "/org/elmlang/intellijplugin/icons/";

    @NotNull
    private static Icon getIcon(String path) {
        return IconLoader.getIcon(ELM_ICONS_PATH + path);
    }

    public static final Icon FILE = getIcon("elm-file.png");
    public static final Icon FUNCTION = getIcon("function.png");
    public static final Icon VALUE = getIcon("value.png");
    public static final Icon UNION_TYPE = getIcon("type.png");
    public static final Icon TYPE_ALIAS = getIcon("type.png");
}