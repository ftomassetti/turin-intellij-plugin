package me.tomassetti.turin.idea.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.parser.TurinLexer;
import me.tomassetti.turin.idea.parser.TurinTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Created by federico on 30/08/15.
 */
public class TurinSyntaxHighligher extends SyntaxHighlighterBase {

    public static final TextAttributesKey NAMESPACE_KW = createTextAttributesKey("NAMESPACE_KW", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    private static final TextAttributesKey[] KEYWORDS_KEYS = new TextAttributesKey[]{NAMESPACE_KW};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[]{};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new TurinLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (TurinTokenTypes.KEYWORDS.contains(tokenType)) {
            return KEYWORDS_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
