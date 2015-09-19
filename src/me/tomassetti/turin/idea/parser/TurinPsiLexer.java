package me.tomassetti.turin.idea.parser;

import me.tomassetti.parser.antlr.TurinLexer;
import me.tomassetti.turin.idea.TurinLanguage;
import me.tomassetti.turin.idea.antlradaptor.lexer.SimpleAntlrAdapter;
import org.antlr.v4.runtime.CharStream;

public class TurinPsiLexer extends SimpleAntlrAdapter {

    public TurinPsiLexer(boolean hideChannel) {
        super(TurinLanguage.INSTANCE, getTurinLexer(hideChannel));
    }

    private static TurinLexer getTurinLexer(boolean hideChannel) {
        TurinLexer turinLexer = new me.tomassetti.parser.antlr.TurinLexer((CharStream) null);
        turinLexer.setUseHiddenChannel(hideChannel);
        return turinLexer;
    }

}
