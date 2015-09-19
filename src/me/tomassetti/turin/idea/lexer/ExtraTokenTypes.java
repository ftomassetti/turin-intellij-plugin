package me.tomassetti.turin.idea.lexer;

import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.psi.TurinTokenType;

/**
 * Token types which do not appear in the Grammar but are emitted from the Lexer
 */
public class ExtraTokenTypes {

    public static final IElementType LINE_COMMENT = new TurinTokenType("LINE_COMMENT");

}
