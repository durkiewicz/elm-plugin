package org.elmlang.intellijplugin;


import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
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

    private static final TokenSet KEYWORDS = TokenSet.create(
            WHERE,
            MODULE,
            IMPORT,
            AS,
            EXPOSING,
            IF,
            THEN,
            ELSE,
            CASE,
            OF,
            LET,
            IN,
            TYPE,
            ALIAS,
            PORT
    );

    private static final TokenSet STRINGS = TokenSet.create(
            STRING_LITERAL,
            CHAR_LITERAL
    );

    private static final TokenSet PARENTHESES = TokenSet.create(
            LEFT_PARENTHESIS,
            RIGHT_PARENTHESIS
    );

    private static final TokenSet COMMENTS = TokenSet.create(
            LINE_COMMENT,
            START_COMMENT,
            END_COMMENT,
            COMMENT_CONTENT
    );

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ElmLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (KEYWORDS.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (PARENTHESES.contains(tokenType)) {
            return PARENTHESIS_KEYS;
        } else if (COMMENTS.contains(tokenType)) {
            return COMMENT_KEYS;
        } else if (STRINGS.contains(tokenType)) {
            return STRING_KEYS;
        } else if (tokenType.equals(NUMBER_LITERAL)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}