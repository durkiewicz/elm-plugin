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

%{
    private IElementType nonInitial(IElementType elementType) {
        yybegin(NON_INITIAL);
        return elementType;
    }
%}

%state NON_INITIAL

CRLF= (\n|\r|\r\n)+
WHITE_SPACE=[\ \t\f]+
LINE_COMMENT=("--")[^\r\n]*
IDENTIFIER_CHAR=[[:letter:][:digit:]_]
LOWER_CASE_IDENTIRIER=[:lowercase:]({IDENTIFIER_CHAR}|')*
UPPER_CASE_IDENTIRIER=[:uppercase:]{IDENTIFIER_CHAR}*
MODULE_PATH=({UPPER_CASE_IDENTIRIER}\.)+{UPPER_CASE_IDENTIRIER}

%%

// YYINITIAL state is when the line hasn't had any lexems yet.

<YYINITIAL> {
    {WHITE_SPACE} {
        return nonInitial(INDENTATION);
    }
}

"module" {
    return nonInitial(MODULE);
}
"where" {
    return nonInitial(WHERE);
}
"import" {
    return nonInitial(IMPORT);
}
"as" {
    return nonInitial(AS);
}
"exposing" {
    return nonInitial(EXPOSING);
}
"(" {
    return nonInitial(LEFT_PARENTHESIS);
}
")" {
    return nonInitial(RIGHT_PARENTHESIS);
}
".." {
    return nonInitial(DOUBLE_DOT);
}
"," {
    return nonInitial(COMMA);
}
{LOWER_CASE_IDENTIRIER} {
    return nonInitial(LOWER_CASE_IDENTIRIER);
}
{MODULE_PATH} {
    return nonInitial(MODULE_PATH);
}
{UPPER_CASE_IDENTIRIER} {
    return nonInitial(UPPER_CASE_IDENTIRIER);
}
{CRLF} {
    yybegin(YYINITIAL);
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
