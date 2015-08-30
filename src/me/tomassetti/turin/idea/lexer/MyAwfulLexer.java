package me.tomassetti.turin.idea.lexer;

import com.google.common.collect.ImmutableMap;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.psi.TurinTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by federico on 30/08/15.
 */
public class MyAwfulLexer extends LexerBase {


    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int state = 0;
    private int currentOffset;

    class Token {
        int start;
        int end;
        IElementType type;
    }

    private Token token = new Token();

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.state = initialState;
        this.currentOffset = startOffset;
        calcToken();
    }

    @Override
    public int getState() {
        return state;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return token.type;
    }

    private Map<String, IElementType> keywordsMap = ImmutableMap.<String, IElementType>builder()
            .put("namespace", TurinTypes.NAMESPACE_KW)
            .put(":",TurinTypes.COLON)
            .put("{", TurinTypes.LBRACKET)
            .put("}", TurinTypes.RBRACKET)
            .put("property", TurinTypes.PROPERTY_KW)
            .put("type", TurinTypes.TYPE_KW)
            .build();

    private void calcToken() {
        token.start = currentOffset;
        if (currentOffset == endOffset) {
            token.end = currentOffset;
            token.type = null;
            return;
        }
        String rest = buffer.subSequence(currentOffset, endOffset).toString();
        for (String keyword : keywordsMap.keySet()) {
            if (rest.startsWith(keyword)) {
                token.end = token.start + keyword.length();
                token.type = keywordsMap.get(keyword);
                currentOffset = token.end;
                return;
            }
        }
        token.end = token.start + 1;
        token.type = TokenType.BAD_CHARACTER;
        currentOffset = token.end ;
        return;
    }

    @Override
    public int getTokenStart() {
        return token.start;
    }

    @Override
    public int getTokenEnd() {
        return token.end;
    }

    @Override
    public void advance() {
        calcToken();
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}
