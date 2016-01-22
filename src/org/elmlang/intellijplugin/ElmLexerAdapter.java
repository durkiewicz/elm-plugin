package org.elmlang.intellijplugin;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class ElmLexerAdapter extends FlexAdapter {
    public ElmLexerAdapter() {
        super(new ElmLexer((Reader) null));
    }
}
