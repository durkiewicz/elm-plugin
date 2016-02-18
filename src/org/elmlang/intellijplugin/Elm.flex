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

    private IElementType setPrevious(IElementType elem) {
        return elem;
    }
%}

%state IN_COMMENT

CRLF= (\n|\r|\r\n)
WHITE_SPACE=[\ \t\f]
LINE_COMMENT=("--")[^\r\n]*
IDENTIFIER_CHAR=[[:letter:][:digit:]_']
LOWER_CASE_IDENTIFIER=[:lowercase:]{IDENTIFIER_CHAR}*
UPPER_CASE_IDENTIFIER=[:uppercase:]{IDENTIFIER_CHAR}*
STRING_LITERAL=\"(\\.|[^\\\"])*\"
STRING_WITH_QUOTES_LITERAL=\"\"\"(\\.|[^\\\"]|\"{1,2}([^\"\\]|\\\"))*\"\"\"
NUMBER_LITERAL=("-")?[:digit:]+(\.[:digit:]+)?
CHAR_LITERAL='(\\.|[^\\'])'
OPERATOR=("!"|"$"|"^"|"|"|"*"|"/"|"?"|"+"|-|=|@|#|%|&|<|>|:|€|¥|¢|£|¤)+
BACKTICKED_FUNCTION="`"{LOWER_CASE_IDENTIFIER}"`"
RESERVED=("hiding" | "export" | "foreign" | "perform" | "deriving")

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
    "if" {
        return setPrevious(IF);
    }
    "then" {
        return setPrevious(THEN);
    }
    "else" {
        return setPrevious(ELSE);
    }
    "case" {
        return setPrevious(CASE);
    }
    "of" {
        return setPrevious(OF);
    }
    "let" {
        return setPrevious(LET);
    }
    "in" {
        return setPrevious(IN);
    }
    "type" {
        return setPrevious(TYPE);
    }
    "alias" {
        return setPrevious(ALIAS);
    }
    "port" {
        return setPrevious(PORT);
    }
    "infixl" {
        return setPrevious(INFIXL);
    }
    "infix" {
        return setPrevious(INFIX);
    }
    "infixr" {
        return setPrevious(INFIXR);
    }
    {RESERVED} {
        return setPrevious(RESERVED);
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
    "->" {
        return setPrevious(ARROW);
    }
    "::" {
        return setPrevious(LIST_CONSTRUCTOR);
    }
    ":" {
        return setPrevious(COLON);
    }
    "|" {
        return setPrevious(PIPE);
    }
    "\\" {
        return setPrevious(BACKSLASH);
    }
    "_" {
        return setPrevious(UNDERSCORE);
    }
    "." {
        return setPrevious(DOT);
    }
    {CRLF}*"{-" {
        commentLevel = 1;
        yybegin(IN_COMMENT);
        return setPrevious(START_COMMENT);
    }
    {LOWER_CASE_IDENTIFIER} {
        return setPrevious(LOWER_CASE_IDENTIFIER);
    }
    {UPPER_CASE_IDENTIFIER} {
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
    ({CRLF}+{WHITE_SPACE}+)+ {
        return setPrevious(TokenType.WHITE_SPACE);
    }
    {CRLF}*{LINE_COMMENT} {
        return setPrevious(LINE_COMMENT);
    }
    {OPERATOR}|{BACKTICKED_FUNCTION} {
        return setPrevious(OPERATOR);
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
