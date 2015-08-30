package me.tomassetti.turin.idea.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import me.tomassetti.parser.antlr.*;
import me.tomassetti.parser.antlr.TurinLexer;
import me.tomassetti.turin.idea.psi.TurinTypes;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.jcip.annotations.Immutable;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.IntegerStack;
import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.misc.ObjectEqualityComparator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by federico on 30/08/15.
 */
public class MyLexerAdapter extends LexerBase {

    private boolean forHighlight = false;

    public boolean isForHighlight() {
        return forHighlight;
    }

    public void setForHighlight(boolean forHighlight) {
        this.forHighlight = forHighlight;
    }

    /**
     * Provides a map from a {@code State} object &rarr; state index tracked by IntelliJ. This field provides for an
     * efficient implementation of {@link #getState}.
     */
    private final Map<AntlrLexerState, Integer> stateCacheMap = new HashMap<AntlrLexerState, Integer>();
    /**
     * Provides a map from a state index tracked by IntelliJ &rarr; {@code State} object describing the ANTLR lexer
     * state. This field provides for an efficient implementation of {@link #toLexerState}.
     */
    private final List<AntlrLexerState> stateCache = new ArrayList<AntlrLexerState>();

    /**
     * This class stores the state of an ANTLR lexer, such that it can be applied back to the lexer instance at a later
     * time.
     *
     * <p>The default implementation stores the following fields, which provides support for any ANTLR 4 single- or
     * multi-mode lexer that does not rely on custom state information for semantic predicates, custom embedded actions,
     * and/or overridden methods such as {@link Lexer#nextToken} or {@link Lexer#emit}.</p>
     *
     * <ul>
     *     <li>{@link Lexer#_mode}: The current lexer mode.</li>
     *     <li>{@link Lexer#_modeStack}: The current lexer mode stack.</li>
     * </ul>
     *
     * <p>If your lexer requires additional information to be stored, this class must be extended in the following ways.</p>
     *
     * <ol>
     *     <li>Override {@link #apply} to ensure that the additional state information is applied to the provided lexer
     *     instance.</li>
     *     <li>Override {@link #hashCodeImpl} and {@link #equals} to ensure that the caching features provided by
     *     {@link AbstractAntlrAdapter} are able to efficiently store the resulting state instances.</li>
     * </ol>
     */
    @Immutable
    public class AntlrLexerState {
        /**
         * This is the backing field for {@link #getMode}.
         */
        private final int mode;
        /**
         * This is the backing field for {@link #getModeStack}.
         */
        @Nullable
        private final int[] modeStack;

        /**
         * This field stores the cached hash code to maximize the efficiency of {@link #hashCode}.
         */
        private int cachedHashCode;

        /**
         * Constructs a new instance of {@link AntlrLexerState} containing the mode and mode stack information for an ANTLR
         * lexer.
         *
         * @param mode The current lexer mode, {@link Lexer#_mode}.
         * @param modeStack The lexer mode stack, {@link Lexer#_modeStack}, or {@code null} .
         */
        public AntlrLexerState(int mode, @Nullable IntegerStack modeStack) {
            this.mode = mode;
            this.modeStack = modeStack != null ? modeStack.toArray() : null;
        }

        /**
         * Gets the value of {@link Lexer#_mode} for the current lexer state.
         *
         * @return The value of {@link Lexer#_mode} for the current lexer state.
         */
        public int getMode() {
            return mode;
        }

        /**
         * Gets the mode stack stored in {@link Lexer#_modeStack} for the current lexer state.
         *
         * @return The mode stack stored in {@link Lexer#_modeStack} for the current lexer state, or {@code null} if the
         * mode stack is empty.
         */
        @Nullable
        public int[] getModeStack() {
            return modeStack;
        }

        public void apply(@NotNull Lexer lexer) {
            lexer._mode = getMode();
            lexer._modeStack.clear();
            if (getModeStack() != null) {
                lexer._modeStack.addAll(getModeStack());
            }
        }

        @Override
        public final int hashCode() {
            if (cachedHashCode == 0) {
                cachedHashCode = hashCodeImpl();
            }

            return cachedHashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof AntlrLexerState)) {
                return false;
            }

            AntlrLexerState other = (AntlrLexerState)obj;
            return this.mode == other.mode
                    && ObjectEqualityComparator.INSTANCE.equals(this.modeStack, other.modeStack);
        }

        protected int hashCodeImpl() {
            int hash = MurmurHash.initialize();
            hash = MurmurHash.update(hash, mode);
            hash = MurmurHash.update(hash, modeStack);
            return MurmurHash.finish(hash, 2);
        }
    }

    class CharSequenceCharStream implements CharStream {
        private final CharSequence buffer;
        /**
         * If greater than or equal to 0, this value overrides the value returned by
         * {@link #buffer}{@code .}{@link CharSequence#length()}.
         */
        private final int endOffset;
        private final String sourceName;

        private int position;

        public CharSequenceCharStream(CharSequence buffer, int endOffset, String sourceName) {
            this.buffer = buffer;
            this.sourceName = sourceName;
            this.endOffset = endOffset;
        }

        protected final CharSequence getBuffer() {
            return buffer;
        }

        protected final int getPosition() {
            return position;
        }

        protected final void setPosition(int position) {
            this.position = position;
        }

        @Override
        public String getText(Interval interval) {
            int start = interval.a;
            int stop = interval.b;
            int n = size();
            if ( stop >= n ) stop = n-1;
            if ( start >= n ) return "";
            return buffer.subSequence(start, stop + 1).toString();
        }

        @Override
        public void consume() {
            if (position == size()) {
                throw new IllegalStateException("attempted to consume EOF");
            }

            position++;
        }

        @Override
        public int LA(int i) {
            if (i > 0) {
                int index = position + i - 1;
                if (index >= size() ) {
                    return IntStream.EOF;
                }

                return buffer.charAt(index);
            }
            else if (i < 0) {
                int index = position + i;
                if (index < 0) {
                    return 0;
                }

                return buffer.charAt(index);
            }
            else {
                return 0;
            }
        }

        @Override
        public int mark() {
            return 0;
        }

        @Override
        public void release(int marker) {
        }

        @Override
        public int index() {
            return position;
        }

        @Override
        public void seek(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("index cannot be negative");
            }

            index = Math.min(index, size());
            position = index;
        }

        @Override
        public int size() {
            if (endOffset >= 0) {
                return endOffset;
            }

            int n = buffer.length();
            return n;
        }

        @Override
        public String getSourceName() {
            return sourceName;
        }
    }

    private me.tomassetti.parser.antlr.TurinLexer antlrLexer = new TurinLexer(null);
    private int startOffset;
    private Token currentToken;
    private CharSequence buffer;
    private int endOffset;
    private AntlrLexerState state;

    /**
     * Update the current lexer to use the specified {@code input} stream starting in the specified {@code state}.
     *
     * <p>The current lexer may be obtained by calling {@link #getLexer}. The default implementation calls
     * {@link Lexer#setInputStream} to set the input stream, followed by {@link AntlrLexerState#apply} to initialize the
     * state of the lexer.</p>
     *
     * @param input The new input stream for the lexer.
     * @param state A {@code State} instance containing the starting state for the lexer.
     */
    protected void applyLexerState(CharStream input) {
        antlrLexer.setInputStream(input);
    }

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.endOffset = endOffset;

        CharStream in = new CharSequenceCharStream(buffer, endOffset, IntStream.UNKNOWN_SOURCE_NAME);
        in.seek(startOffset);

        AntlrLexerState state;
        if (startOffset == 0 && initialState == 0) {
            state = getInitialState();
        } else {
            state = toLexerState(initialState);
        }

        applyLexerState(in, state);

        System.out.println("FEDERICO start "+startOffset+" "+endOffset+" "+initialState+" (buffer size "+buffer.length()+")");
        /*String input = buffer.subSequence(startOffset, endOffset).toString();
        ANTLRInputStream ai = new ANTLRInputStream(input);
        antlrLexer = new TurinLexer(ai);
        this.startOffset = startOffset;
        antlrLexer.setState(initialState);


        currentToken = antlrLexer.nextToken();*/
        advance();
    }

    @Override
    public int getState() {
        AntlrLexerState state = this.state != null ? this.state : getInitialState();
        Integer existing = stateCacheMap.get(state);
        if (existing == null) {
            existing = stateCache.size();
            stateCache.add(state);
            stateCacheMap.put(state, existing);
        }

        return existing;
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
            case TurinLexer.NL:
                return TurinTypes.NL;
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
            case TurinLexer.LBRACKET:
                return TurinTypes.LBRACKET;
            case TurinLexer.RBRACKET:
                return TurinTypes.RBRACKET;
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
        return currentToken.getStartIndex();
    }

    @Override
    public int getTokenEnd() {
        return currentToken.getStopIndex() + 1;
    }

    /**
     * Update the current lexer to use the specified {@code input} stream starting in the specified {@code state}.
     *
     * <p>The current lexer may be obtained by calling {@link #getLexer}. The default implementation calls
     * {@link Lexer#setInputStream} to set the input stream, followed by {@link AntlrLexerState#apply} to initialize the
     * state of the lexer.</p>
     *
     * @param input The new input stream for the lexer.
     * @param state A {@code State} instance containing the starting state for the lexer.
     */
    protected void applyLexerState(CharStream input, AntlrLexerState state) {
        antlrLexer.setInputStream(input);
        state.apply(antlrLexer);
    }

    /**
     * Get the initial {@code State} of the lexer.
     *
     * @return a {@code State} instance representing the state of the lexer at the beginning of an input.
     */
    protected AntlrLexerState getInitialState() {
        return new AntlrLexerState(Lexer.DEFAULT_MODE, null);
    }

    /**
     * Get a {@code State} instance representing the current state of the specified lexer.
     *
     * @param lexer The lexer.
     * @return A {@code State} instance containing the current state of the lexer.
     */
    protected AntlrLexerState getLexerState(Lexer lexer) {
        if (lexer._modeStack.isEmpty()) {
            return new AntlrLexerState(lexer._mode, null);
        }

        return new AntlrLexerState(lexer._mode, lexer._modeStack);
    }

    /**
     * Gets the {@code State} corresponding to the specified IntelliJ {@code state}.
     *
     * @param state The lexer state provided by IntelliJ.
     * @return The {@code State} instance corresponding to the specified state.
     */
    protected AntlrLexerState toLexerState(int state) {
        return stateCache.get(state);
    }

    @Override
    public void advance() {
        state = getLexerState(antlrLexer);
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
