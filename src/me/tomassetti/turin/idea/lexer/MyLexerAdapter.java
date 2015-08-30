package me.tomassetti.turin.idea.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import me.tomassetti.parser.antlr.*;
import me.tomassetti.parser.antlr.TurinLexer;
import me.tomassetti.turin.idea.psi.TurinTypes;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by federico on 30/08/15.
 */
public class MyLexerAdapter extends LexerBase {

    private me.tomassetti.parser.antlr.TurinLexer antlrLexer = null;
    private int startOffset;
    private Token currentToken;
    private CharSequence buffer;
    private int endOffset;


    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        System.out.println("FEDERICO start "+startOffset+" "+endOffset+" "+initialState+" (buffer size "+buffer.length()+")");
        String input = buffer.subSequence(startOffset, endOffset).toString();
        ANTLRInputStream ai = new ANTLRInputStream(input);
        antlrLexer = new TurinLexer(ai);
        this.startOffset = startOffset;
        antlrLexer.setState(initialState);
        this.buffer = buffer;
        this.endOffset = endOffset;

        currentToken = antlrLexer.nextToken();
    }

    @Override
    public int getState() {
        int state = antlrLexer == null ? 0 : antlrLexer.getState();
        System.out.println("FEDERICO getState " + state);
        return state;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        System.out.println("FEDERICO getTokenType " + getTokenTypeHelper());
        return getTokenTypeHelper();
    }

    private IElementType getTokenTypeHelper() {
        if (currentToken == null) return null;
        switch (currentToken.getType()) {
            case TurinLexer.EOF:
                return null;
            case TurinLexer.ID:
                return TurinTypes.ID;
            case TurinLexer.TID:
                return TurinTypes.TID;
            case TurinLexer.COLON:
                return TurinTypes.COLON;
            case TurinLexer.NAMESPACE_KW:
                return TurinTypes.NAMESPACE_KW;
            case TurinLexer.PROPERTY_KW:
                return TurinTypes.PROPERTY_KW;
            case TurinLexer.TYPE_KW:
                return TurinTypes.TYPE_KW;
            case TurinLexer.PROGRAM_KW:
            case TurinLexer.POINT:
            case TurinLexer.LINE_COMMENT:
            case TurinLexer.ASSIGNMENT:
            default:
                return TokenType.BAD_CHARACTER;
        }
    }

    @Override
    public int getTokenStart() {
        int res = currentToken == null ? startOffset : currentToken.getStartIndex() + startOffset;
        System.out.println("FEDERICO getTokenStart " + res);
        return res;
    }

    @Override
    public int getTokenEnd() {
        int res = currentToken.getStopIndex() + startOffset + 1;
        System.out.println("FEDERICO getTokenEnd " + res);
        return res;
    }

    @Override
    public void advance() {
        currentToken = antlrLexer.nextToken();
        System.out.println("FEDERICO advance " + currentToken);
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        System.out.println("FEDERICO getBufferSequence");
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        System.out.println("FEDERICO getBufferEnd " + endOffset);
        return endOffset;
    }
}
