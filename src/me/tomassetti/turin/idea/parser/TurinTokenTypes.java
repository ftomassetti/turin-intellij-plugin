package me.tomassetti.turin.idea.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import me.tomassetti.parser.antlr.*;
import me.tomassetti.parser.antlr.TurinLexer;
import me.tomassetti.turin.idea.TurinLanguage;
import me.tomassetti.turin.idea.antlradaptor.lexer.ElementTypeFactory;
import me.tomassetti.turin.idea.antlradaptor.lexer.RuleElementType;
import me.tomassetti.turin.idea.antlradaptor.lexer.TokenElementType;
import org.intellij.lang.annotations.MagicConstant;

import java.util.Arrays;
import java.util.List;

public class TurinTokenTypes {

    public static IElementType BAD_TOKEN_TYPE = new IElementType("BAD_TOKEN", TurinLanguage.getInstance());

    public static final List<TokenElementType> TOKEN_ELEMENT_TYPES =
            ElementTypeFactory.getTokenElementTypes(TurinLanguage.getInstance(),
                    Arrays.asList(TurinParser.tokenNames));
    public static final List<RuleElementType> RULE_ELEMENT_TYPES =
            ElementTypeFactory.getRuleElementTypes(TurinLanguage.getInstance(),
                    Arrays.asList(TurinParser.ruleNames));

    public static final TokenSet STRINGS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.STRING_START,
                    TurinLexer.STRING_STOP,
                    TurinLexer.STRING_CONTENT);

    public static final TokenSet NUMBERS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.INT);

    public static final TokenSet BRACKETS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.LBRACKET,
                    TurinLexer.RBRACKET);

    public static final TokenSet COMMENTS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    me.tomassetti.parser.antlr.TurinLexer.LINE_COMMENT);

    public static final TokenSet WHITESPACES =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    me.tomassetti.parser.antlr.TurinLexer.WS);

    public static final TokenSet KEYWORDS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.NAMESPACE_KW,
                    TurinLexer.TYPE_KW,
                    TurinLexer.VAL_KW,
                    TurinLexer.PROPERTY_KW,
                    TurinLexer.ABSTRACT_KW,
                    TurinLexer.AND_KW,
                    TurinLexer.OR_KW,
                    TurinLexer.NOT_KW,
                    TurinLexer.AS_KW,
                    TurinLexer.HAS_KW,
                    TurinLexer.CATCH_KW,
                    TurinLexer.TRY_KW,
                    TurinLexer.THROW_KW,
                    TurinLexer.IF_KW,
                    TurinLexer.ELIF_KW,
                    TurinLexer.ELSE_KW,
                    TurinLexer.TRUE_KW,
                    TurinLexer.FALSE_KW,
                    TurinLexer.PROGRAM_KW,
                    TurinLexer.RETURN_KW,
                    TurinLexer.SHARED_KW,
                    TurinLexer.IMPORT_KW,
                    TurinLexer.DEFAULT_KW,
                    TurinLexer.PRIMITIVE_TYPE,
                    TurinLexer.BASIC_TYPE,
                    TurinLexer.VOID_KW);

    public static final TokenSet VALUE_IDENTIFIERS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.VALUE_ID);

    public static final TokenSet TYPE_IDENTIFIERS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.TYPE_ID);

    public static final TokenSet ANNOTATIONS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.getInstance(),
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.ANNOTATION_ID);

    public static RuleElementType getRuleElementType(@MagicConstant(valuesFromClass = TurinParser.class)int ruleIndex){
        return RULE_ELEMENT_TYPES.get(ruleIndex);
    }
    public static TokenElementType getTokenElementType(@MagicConstant(valuesFromClass = TurinParser.class)int ruleIndex){
        return TOKEN_ELEMENT_TYPES.get(ruleIndex);
    }

}
