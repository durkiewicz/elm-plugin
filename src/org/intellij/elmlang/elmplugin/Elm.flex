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

%state NON_INITIAL

CRLF= (\n|\r|\r\n)+
WHITE_SPACE=[\ \t\f]+
LINE_COMMENT=("--")[^\r\n]*
IDENTIFIER_CHAR=[[:letter:][:digit:]_]
LOWER_CASE_IDENTIRIER=[:lowercase:]({IDENTIFIER_CHAR}|')*
UPPER_CASE_IDENTIRIER=[:uppercase:]{IDENTIFIER_CHAR}*
MODULE_NAME=({UPPER_CASE_IDENTIRIER}\.)*{UPPER_CASE_IDENTIRIER}

%%

// YYINITIAL state is when the line hasn't had any lexems yet.

<YYINITIAL> {
    {WHITE_SPACE} {
        yybegin(NON_INITIAL);
        return INDENTATION;
    }
}

"module" {
    yybegin(NON_INITIAL);
    return MODULE;
}
"where" {
    yybegin(NON_INITIAL);
    return WHERE;
}
{LOWER_CASE_IDENTIRIER} {
    yybegin(NON_INITIAL);
    return LOWER_CASE_IDENTIRIER;
}
{MODULE_NAME} {
    yybegin(NON_INITIAL);
    return MODULE_NAME;
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
