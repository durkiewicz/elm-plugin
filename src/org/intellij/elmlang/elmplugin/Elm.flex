package org.intellij.elmlang.elmplugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import static org.intellij.elmlang.elmplugin.psi.ElmTypes.*;

%%

%class ElmLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF= (\n|\r|\r\n)+
WHITE_SPACE=[\ \t\f]+
LINE_COMMENT=("--")[^\r\n]*
IDENTIFIER_CHAR=[[:letter:][:digit:]_]
LOWER_CASE_IDENTIRIER=[:lowercase:]({IDENTIFIER_CHAR}|')*
UPPER_CASE_IDENTIRIER=[:uppercase:]{IDENTIFIER_CHAR}*
MODULE_PATH=({UPPER_CASE_IDENTIRIER}\.)+{UPPER_CASE_IDENTIRIER}

%%

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
{LOWER_CASE_IDENTIRIER} {
    return LOWER_CASE_IDENTIRIER;
}
{MODULE_PATH} {
    return MODULE_PATH;
}
{UPPER_CASE_IDENTIRIER} {
    return UPPER_CASE_IDENTIRIER;
}
({CRLF}{WHITE_SPACE}) {
    return TokenType.WHITE_SPACE;
}
{CRLF} {
    return NEW_LINE;
}
{WHITE_SPACE} {
    return TokenType.WHITE_SPACE;
}
{LINE_COMMENT} {
    return LINE_COMMENT;
}
. {
    return TokenType.BAD_CHARACTER;
}
