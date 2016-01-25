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
%}

%state IN_COMMENT

CRLF= (\n|\r|\r\n)
WHITE_SPACE=[\ \t\f]
LINE_COMMENT=("--")[^\r\n]*
IDENTIFIER_CHAR=[[:letter:][:digit:]_]
LOWER_CASE_IDENTIRIER=[:lowercase:]({IDENTIFIER_CHAR}|')*
UPPER_CASE_IDENTIFIER=[:uppercase:]{IDENTIFIER_CHAR}*
MODULE_PATH=({UPPER_CASE_IDENTIFIER}\.)+{UPPER_CASE_IDENTIFIER}
STRING_LITERAL=\"(\\.|[^\\\"])*\"
STRING_WITH_QUOTES_LITERAL=\"\"\"(\\.|[^\\\"]|\"{1,2}([^\"\\]|\\\"))*\"\"\"
NUMBER_LITERAL=("-")?[:digit:]+(\.[:digit:]+)?
CHAR_LITERAL='(\\.|[^\\'])'

%%

<IN_COMMENT> {
    "{-" {
        commentLevel++;
        return COMMENT_CONTENT;
    }
    "-}" {
        commentLevel--;
        if (commentLevel == 0) {
            yybegin(YYINITIAL);
            return END_COMMENT;
        }
        return COMMENT_CONTENT;
    }
    [^-{}]+ {
        return COMMENT_CONTENT;
    }
    [^] {
        return COMMENT_CONTENT;
    }
}


<YYINITIAL> {
    "module" {
        return MODULE;
    }
    "where" {
        return WHERE;
    }
    "import" {
        return IMPORT;
    }
    "as" {
        return AS;
    }
    "exposing" {
        return EXPOSING;
    }
    "(" {
        return LEFT_PARENTHESIS;
    }
    ")" {
        return RIGHT_PARENTHESIS;
    }
    ".." {
        return DOUBLE_DOT;
    }
    "," {
        return COMMA;
    }
    "=" {
        return EQ;
    }
    {CRLF}*"{-" {
        commentLevel = 1;
        yybegin(IN_COMMENT);
        return START_COMMENT;
    }
    {LOWER_CASE_IDENTIRIER} {
        return LOWER_CASE_IDENTIRIER;
    }
    {MODULE_PATH} {
        return MODULE_PATH;
    }
    {UPPER_CASE_IDENTIFIER} {
        return UPPER_CASE_IDENTIFIER;
    }
    {STRING_WITH_QUOTES_LITERAL} {
        return STRING_LITERAL;
    }
    {STRING_LITERAL} {
        return STRING_LITERAL;
    }
    {CHAR_LITERAL} {
        return CHAR_LITERAL;
    }
    {NUMBER_LITERAL} {
        return NUMBER_LITERAL;
    }
    ({CRLF}+{WHITE_SPACE}+) {
        return TokenType.WHITE_SPACE;
    }
    {CRLF}*{LINE_COMMENT} {
        return LINE_COMMENT;
    }
    {WHITE_SPACE}+ {
        return TokenType.WHITE_SPACE;
    }
    {CRLF}+ {
        return FRESH_LINE;
    }
}

. {
    return TokenType.BAD_CHARACTER;
}
