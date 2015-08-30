package me.tomassetti.turin.idea.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static me.tomassetti.turin.idea.psi.TurinTypes.*;

%%

%class TurinLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF= \n|\r|\r\n
WHITE_SPACE=[\ \t\f]+
END_OF_LINE_COMMENT=("//")[^\r\n]*

NAMESPACE_KW="namespace"
PROPERTY_KW="property"
TYPE_KW="type"
//VAL_KW="val"
ID=[a-z][a-zA-Z0-9_]*
TID=[A-Z][a-zA-Z0-9_]*
LBRACKET="{"
RBRACKET="}"
LPAREN="("
RPAREN=")"
LSQUARE="["
RSQUARE="]"
COMMA=","
COLON=":"

//%state WAITING_VALUE

%%

//<YYINITIAL> {END_OF_LINE_COMMENT}                           { return LINE_COMMENT; }


<YYINITIAL> {WHITE_SPACE} {System.out.println("WHITESPACE");}
<YYINITIAL> {CRLF}                           { return NL; }
<YYINITIAL> {NAMESPACE_KW}                           { return NAMESPACE_KW; }
<YYINITIAL> {PROPERTY_KW}                           { return PROPERTY_KW; }
<YYINITIAL> {TYPE_KW}                           { return TYPE_KW; }
//<YYINITIAL> {VAL_KW}                           { return VAL_KW; }
<YYINITIAL> {ID}                           { return ID; }
<YYINITIAL> {TID}                           { return TID; }
<YYINITIAL> {LBRACKET}                           { return LBRACKET; }
<YYINITIAL> {RBRACKET}                           { return RBRACKET; }

//<YYINITIAL> {LPAREN}                           { return LPAREN; }

//<YYINITIAL> {RPAREN}                           { return RPAREN; }

//<YYINITIAL> {LSQUARE}                           { return LSQUARE; }

//<YYINITIAL> {RSQUARE}                           { return RSQUARE; }

//<YYINITIAL> {COMMA}                           { return COMMA; }

<YYINITIAL> {COLON}                           { return COLON; }

<YYINITIAL> [^]                                { System.out.println("BAD_STUFF"); return TokenType.BAD_CHARACTER; }