package me.tomassetti.turin.idea.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.psi.TurinTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by federico on 30/08/15.
 */
public class MyAwfulLexer extends LexerBase {


    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int state = 0;
    private int currentOffset;

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.state = initialState;
        this.currentOffset = startOffset;
    }

    @Override
    public int getState() {
        return state;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        if (currentOffset == buffer.length() || currentOffset==endOffset) {
            return null;
        }
        return TurinTypes.ID;
    }

    @Override
    public int getTokenStart() {
        return currentOffset;
    }

    @Override
    public int getTokenEnd() {
        return currentOffset + 1;
    }

    @Override
    public void advance() {
        currentOffset++;
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
