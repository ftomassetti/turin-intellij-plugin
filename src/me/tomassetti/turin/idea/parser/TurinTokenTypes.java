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

/**
 * Created by federico on 30/08/15.
 */
public class TurinTokenTypes {

    public static IElementType BAD_TOKEN_TYPE = new IElementType("BAD_TOKEN", TurinLanguage.INSTANCE);

    public static final List<TokenElementType> TOKEN_ELEMENT_TYPES =
            ElementTypeFactory.getTokenElementTypes(TurinLanguage.INSTANCE,
                    Arrays.asList(TurinParser.tokenNames));
    public static final List<RuleElementType> RULE_ELEMENT_TYPES =
            ElementTypeFactory.getRuleElementTypes(TurinLanguage.INSTANCE,
                    Arrays.asList(TurinParser.ruleNames));

    public static final TokenSet COMMENTS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.INSTANCE,
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    me.tomassetti.parser.antlr.TurinLexer.LINE_COMMENT);

    public static final TokenSet WHITESPACES =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.INSTANCE,
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    me.tomassetti.parser.antlr.TurinLexer.WS);

    public static final TokenSet KEYWORDS =
            ElementTypeFactory.createTokenSet(
                    TurinLanguage.INSTANCE,
                    Arrays.asList(me.tomassetti.parser.antlr.TurinLexer.tokenNames),
                    TurinLexer.NAMESPACE_KW);

    public static RuleElementType getRuleElementType(@MagicConstant(valuesFromClass = TurinParser.class)int ruleIndex){
        return RULE_ELEMENT_TYPES.get(ruleIndex);
    }
    public static TokenElementType getTokenElementType(@MagicConstant(valuesFromClass = TurinParser.class)int ruleIndex){
        return TOKEN_ELEMENT_TYPES.get(ruleIndex);
    }

}
