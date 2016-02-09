package org.elmlang.intellijplugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import static org.elmlang.intellijplugin.psi.ElmTypes.*;

%%

%class ElmLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
    private int commentLevel = 0;
    private IElementType previous = null;

    private IElementType setPrevious(IElementType elem) {
        this.previous = elem;
        return elem;
    }
%}

%state IN_COMMENT

CRLF= (\n|\r|\r\n)
WHITE_SPACE=[\ \t\f]
LINE_COMMENT=("--")[^\r\n]*
IDENTIFIER_CHAR=[[:letter:][:digit:]_]
LOWER_CASE_IDENTIRIER=[:lowercase:]({IDENTIFIER_CHAR}|')*
UPPER_CASE_IDENTIFIER=[:uppercase:]{IDENTIFIER_CHAR}*
STRING_LITERAL=\"(\\.|[^\\\"])*\"
STRING_WITH_QUOTES_LITERAL=\"\"\"(\\.|[^\\\"]|\"{1,2}([^\"\\]|\\\"))*\"\"\"
NUMBER_LITERAL=("-")?[:digit:]+(\.[:digit:]+)?
CHAR_LITERAL='(\\.|[^\\'])'
OPERATOR=("!"|"$"|"^"|"|"|"*"|"/"|"?"|"+"|-|@|#|%|&|<|>|:|€|¥|¢|£|¤)+
BACKTICKED_FUNCTION="`"{LOWER_CASE_IDENTIRIER}"`"

%%

<IN_COMMENT> {
    "{-" {
        commentLevel++;
        return setPrevious(COMMENT_CONTENT);
    }
    "-}" {
        commentLevel--;
        if (commentLevel == 0) {
            yybegin(YYINITIAL);
            return setPrevious(END_COMMENT);
        }
        return setPrevious(COMMENT_CONTENT);
    }
    [^-{}]+ {
        return setPrevious(COMMENT_CONTENT);
    }
    [^] {
        return setPrevious(COMMENT_CONTENT);
    }
}


<YYINITIAL> {
    "module" {
        return setPrevious(MODULE);
    }
    "where" {
        return setPrevious(WHERE);
    }
    "import" {
        return setPrevious(IMPORT);
    }
    "as" {
        return setPrevious(AS);
    }
    "exposing" {
        return setPrevious(EXPOSING);
    }
    "(" {
        return setPrevious(LEFT_PARENTHESIS);
    }
    ")" {
        return setPrevious(RIGHT_PARENTHESIS);
    }
    "[" {
        return setPrevious(LEFT_SQUARE_BRACKET);
    }
    "]" {
        return setPrevious(RIGHT_SQUARE_BRACKET);
    }
    "{" {
        return setPrevious(LEFT_BRACE);
    }
    "}" {
        return setPrevious(RIGHT_BRACE);
    }
    ".." {
        return setPrevious(DOUBLE_DOT);
    }
    "," {
        return setPrevious(COMMA);
    }
    "=" {
        return setPrevious(EQ);
    }
    "::" {
        return setPrevious(LIST_CONSTRUCTOR);
    }
    "|" {
        return setPrevious(PIPE);
    }
    "." {
        if (LOWER_CASE_IDENTIRIER.equals(previous)
            || UPPER_CASE_IDENTIFIER.equals(previous)
            || LOWER_CASE_PATH.equals(previous)
            || UPPER_CASE_PATH.equals(previous)) {
            return setPrevious(DOT_IN_PATH);
        }
        return setPrevious(DOT);
    }
    {CRLF}*"{-" {
        commentLevel = 1;
        yybegin(IN_COMMENT);
        return setPrevious(START_COMMENT);
    }
    {LOWER_CASE_IDENTIRIER} {
        if (DOT.equals(previous)
            || DOT_IN_PATH.equals(previous)) {
            return setPrevious(LOWER_CASE_PATH);
        }
        return setPrevious(LOWER_CASE_IDENTIRIER);
    }
    {UPPER_CASE_IDENTIFIER} {
        if (DOT.equals(previous)
            || DOT_IN_PATH.equals(previous)) {
            return setPrevious(UPPER_CASE_PATH);
        }
        return setPrevious(UPPER_CASE_IDENTIFIER);
    }
    {STRING_WITH_QUOTES_LITERAL} {
        return setPrevious(STRING_LITERAL);
    }
    {STRING_LITERAL} {
        return setPrevious(STRING_LITERAL);
    }
    {CHAR_LITERAL} {
        return setPrevious(CHAR_LITERAL);
    }
    {NUMBER_LITERAL} {
        return setPrevious(NUMBER_LITERAL);
    }
    {OPERATOR}|{BACKTICKED_FUNCTION} {
        return setPrevious(OPERATOR);
    }
    ({CRLF}+{WHITE_SPACE}+) {
        return setPrevious(TokenType.WHITE_SPACE);
    }
    {CRLF}*{LINE_COMMENT} {
        return setPrevious(LINE_COMMENT);
    }
    {WHITE_SPACE}+ {
        return setPrevious(TokenType.WHITE_SPACE);
    }
    {CRLF}+ {
        return setPrevious(FRESH_LINE);
    }
}

. {
    return setPrevious(TokenType.BAD_CHARACTER);
}
