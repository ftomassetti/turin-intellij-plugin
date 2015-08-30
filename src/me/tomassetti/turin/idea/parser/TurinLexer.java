package me.tomassetti.turin.idea.parser;

import me.tomassetti.turin.idea.TurinLanguage;
import me.tomassetti.turin.idea.antlradaptor.lexer.SimpleAntlrAdapter;
import org.antlr.v4.runtime.CharStream;

/**
 * Created by federico on 30/08/15.
 */
public class TurinLexer extends SimpleAntlrAdapter {
    public TurinLexer() {
        super(TurinLanguage.INSTANCE, new me.tomassetti.parser.antlr.TurinLexer((CharStream) null));
    }
}
