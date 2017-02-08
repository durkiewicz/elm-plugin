package org.elmlang.intellijplugin.features.syntaxHighlighting;


import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.elmlang.intellijplugin.ElmLexerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.elmlang.intellijplugin.psi.ElmTypes.*;


class ElmSyntaxHighlighter extends SyntaxHighlighterBase {

    static final TextAttributesKey ELM_KEYWORD =
            createTextAttributesKey("ELM_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    private static final TextAttributesKey ELM_BAD_CHAR =
            createTextAttributesKey("ELM_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey ELM_COMMENT =
            createTextAttributesKey("ELM_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    static final TextAttributesKey ELM_STRING =
            createTextAttributesKey("ELM_STRING", DefaultLanguageHighlighterColors.STRING);

    static final TextAttributesKey ELM_NUMBER =
            createTextAttributesKey("ELM_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    static final TextAttributesKey ELM_DOT =
            createTextAttributesKey("ELM_DOT", DefaultLanguageHighlighterColors.DOT);

    static final TextAttributesKey ELM_ARROW =
            createTextAttributesKey("ELM_ARROW", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    static final TextAttributesKey ELM_OPERATOR =
            createTextAttributesKey("ELM_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    static final TextAttributesKey ELM_PARENTHESIS =
            createTextAttributesKey("ELM_PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES);

    static final TextAttributesKey ELM_BRACES =
            createTextAttributesKey("ELM_BRACES", DefaultLanguageHighlighterColors.BRACES);

    static final TextAttributesKey ELM_BRACKETS =
            createTextAttributesKey("ELM_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);

    static final TextAttributesKey ELM_COMMA =
            createTextAttributesKey("ELM_COMMA", DefaultLanguageHighlighterColors.COMMA);

    static final TextAttributesKey ELM_EQ =
            createTextAttributesKey("ELM_EQ", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    static final TextAttributesKey ELM_PIPE =
            createTextAttributesKey("ELM_PIPE", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    /**
     * The name of a definition.
     *
     * e.g. 'foo' in 'foo x y = x * y'
     */
    static final TextAttributesKey ELM_DEFINITION_NAME =
            createTextAttributesKey("ELM_DEFINITION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);

    /**
     * The uppercase identifier for a type in all contexts EXCEPT when appearing
     * in a type annotation.
     */
    static final TextAttributesKey ELM_TYPE =
            createTextAttributesKey("ELM_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME);

    /**
     * The lowercase identifier name in a type annotation.
     *
     * e.g. 'foo' in 'foo : String -> Cmd msg'
     */
    static final TextAttributesKey ELM_TYPE_ANNOTATION_NAME =
            createTextAttributesKey("ELM_TYPE_ANNOTATION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);

    /**
     * Both uppercase and lowercase identifiers appearing on the right-hand side
     * of a top-level type annotation.
     *
     * e.g. 'String' and 'Cmd msg' in 'foo : String -> Cmd msg'
     */
    static final TextAttributesKey ELM_TYPE_ANNOTATION_SIGNATURE_TYPES =
            createTextAttributesKey("ELM_TYPE_ANNOTATION_SIGNATURE_TYPES", DefaultLanguageHighlighterColors.CLASS_REFERENCE);


    private static final TokenSet KEYWORDS = TokenSet.create(
            WHERE,
            EFFECT,
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
            PORT,
            INFIXL,
            INFIX,
            INFIXR,
            RESERVED
    );

    private static final TokenSet STRINGS = TokenSet.create(
            STRING_LITERAL,
            CHAR_LITERAL
    );

    private static final TokenSet PARENTHESES = TokenSet.create(
            LEFT_PARENTHESIS,
            RIGHT_PARENTHESIS
    );

    private static final TokenSet BRACES = TokenSet.create(
            LEFT_BRACE,
            RIGHT_BRACE
    );

    private static final TokenSet BRACKETS = TokenSet.create(
            LEFT_SQUARE_BRACKET,
            RIGHT_SQUARE_BRACKET
    );

    private static final TokenSet COMMENTS = TokenSet.create(
            LINE_COMMENT,
            START_COMMENT,
            START_DOC_COMMENT,
            END_COMMENT,
            COMMENT_CONTENT
    );

    private static final TokenSet OPERATORS = TokenSet.create(
            OPERATOR,
            LIST_CONSTRUCTOR
    );

    private static final Map<IElementType, TextAttributesKey> keys = new HashMap<>();

    static {
        fillMap(keys, KEYWORDS, ELM_KEYWORD);
        fillMap(keys, STRINGS, ELM_STRING);
        fillMap(keys, COMMENTS, ELM_COMMENT);
        fillMap(keys, PARENTHESES, ELM_PARENTHESIS);
        fillMap(keys, BRACES, ELM_BRACES);
        fillMap(keys, BRACKETS, ELM_BRACKETS);
        fillMap(keys, OPERATORS, ELM_OPERATOR);
        keys.put(ARROW, ELM_ARROW);
        keys.put(EQ, ELM_EQ);
        keys.put(COMMA, ELM_COMMA);
        keys.put(DOT, ELM_DOT);
        keys.put(NUMBER_LITERAL, ELM_NUMBER);
        keys.put(PIPE, ELM_PIPE);
        keys.put(TokenType.BAD_CHARACTER, ELM_BAD_CHAR);
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(keys.get(tokenType));
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ElmLexerAdapter();
    }
}