package org.elmlang.intellijplugin;


import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import static org.elmlang.intellijplugin.psi.ElmTypes.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class ElmSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[] {
        createTextAttributesKey("ELM_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
    };
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[] {
        createTextAttributesKey("ELM_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
    };
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[] {
        createTextAttributesKey("ELM_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    };
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[] {
        createTextAttributesKey("ELM_STRING", DefaultLanguageHighlighterColors.STRING)
    };
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[] {
            createTextAttributesKey("ELM_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
    };
    private static final TextAttributesKey[] PARENTHESIS_KEYS = new TextAttributesKey[] {
        createTextAttributesKey("ELM_PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES)
    };
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ElmLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (isKeyword(tokenType)) {
            return KEYWORD_KEYS;
        } else if (isParenthesis(tokenType)) {
            return PARENTHESIS_KEYS;
        } else if (isComment(tokenType)) {
            return COMMENT_KEYS;
        } else if (isString(tokenType)) {
            return STRING_KEYS;
        } else if (tokenType.equals(NUMBER_LITERAL)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }

    private boolean isKeyword(IElementType tokenType) {
        return tokenType.equals(WHERE) ||
                tokenType.equals(MODULE) ||
                tokenType.equals(IMPORT) ||
                tokenType.equals(AS) ||
                tokenType.equals(EXPOSING);
    }

    private boolean isString(IElementType tokenType) {
        return tokenType.equals(STRING_LITERAL) ||
                tokenType.equals(CHAR_LITERAL);
    }

    private boolean isParenthesis(IElementType tokenType) {
        return tokenType.equals(LEFT_PARENTHESIS) ||
                tokenType.equals(RIGHT_PARENTHESIS);
    }

    private boolean isComment(IElementType tokenType) {
        return tokenType.equals(LINE_COMMENT) ||
                tokenType.equals(START_COMMENT) ||
                tokenType.equals(END_COMMENT) ||
                tokenType.equals(COMMENT_CONTENT);
    }
}