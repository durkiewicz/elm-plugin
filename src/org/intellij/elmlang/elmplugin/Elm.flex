package org.intellij.elmlang.elmplugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.intellij.elmlang.elmplugin.psi.ElmTypes;

%%

%class ElmLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF= \n|\r|\r\n
WHITE_SPACE=[\ \t\f]
FIRST_VALUE_CHARACTER=[^ \n\r\f\\] | "\\"{CRLF} | "\\".
VALUE_CHARACTER=[^\n\r\f\\] | "\\"{CRLF} | "\\".
END_OF_LINE_COMMENT=("--")[^\r\n]*
KEY_CHARACTER=[^:=\ \n\r\t\f\\] | "\\ "
NON_WHITE=[a-z]+

%state WAITING_VALUE

%%

({CRLF}|{WHITE_SPACE})+ { return TokenType.WHITE_SPACE; }

{NON_WHITE} { return ElmTypes.IDENTIFIER; }

{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }

{END_OF_LINE_COMMENT} { return ElmTypes.COMMENT; }

. { return TokenType.BAD_CHARACTER; }
