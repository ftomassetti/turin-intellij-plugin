package me.tomassetti.turin.idea;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import me.tomassetti.parser.antlr.TurinParser;
import me.tomassetti.turin.idea.antlradaptor.parser.AntlrParser;
import me.tomassetti.turin.idea.antlradaptor.parser.SyntaxErrorListener;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

class TurinPsiParser extends AntlrParser<TurinParser> {

    public TurinPsiParser() {
        super(TurinLanguage.getInstance());
    }

    @Override
    protected TurinParser createParserImpl(TokenStream tokenStream, IElementType root, PsiBuilder builder) {
        TurinParser turinParser = new TurinParser(tokenStream);
        turinParser.removeErrorListeners();
        turinParser.addErrorListener(new SyntaxErrorListener());
        return  turinParser;
    }

    @Override
    protected ParseTree parseImpl(TurinParser parser, IElementType root, PsiBuilder builder) {
        int startRule;
        if (root instanceof IFileElementType) {
            startRule = TurinParser.RULE_turinFile;
        }
        else {
            startRule = Token.INVALID_TYPE;
        }

        switch (startRule) {
            case TurinParser.RULE_turinFile:
                return parser.turinFile();

            default:
                String ruleName = TurinParser.ruleNames[startRule];
                throw new UnsupportedOperationException(String.format("cannot start parsing using root element %s", root));
        }
    }
}
