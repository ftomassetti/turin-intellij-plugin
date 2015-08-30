package me.tomassetti.turin.idea.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;

import java.io.Reader;

/**
 * Created by federico on 30/08/15.
 */
public class TurinLexerAdapter extends FlexAdapter {
    public TurinLexerAdapter() {
        super(new TurinLexer((Reader)null));
    }
}
