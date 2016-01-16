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
END_OF_LINE_COMMENT=("--")[^\r\n]*
NON_WHITE=[a-z]+

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
{NON_WHITE} {
    yybegin(NON_INITIAL);
    return IDENTIFIER;
}
{CRLF} {
    yybegin(YYINITIAL);
    return NEW_LINE;
}
{WHITE_SPACE} {
    return TokenType.WHITE_SPACE;
}
{END_OF_LINE_COMMENT} {
    return COMMENT;
}
. {
    return TokenType.BAD_CHARACTER;
}
