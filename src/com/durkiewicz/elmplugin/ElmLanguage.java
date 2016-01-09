package com.durkiewicz.elmplugin;

import com.intellij.lang.Language;

public class ElmLanguage extends Language {
    public static final ElmLanguage INSTANCE = new ElmLanguage();

    private ElmLanguage() {
        super("Elm");
    }
}