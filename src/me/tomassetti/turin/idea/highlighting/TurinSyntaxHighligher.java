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

    public static final TextAttributesKey KEYWORD= createTextAttributesKey("TURIN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("TURIN_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("TURIN_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    private static final TextAttributesKey[] KEYWORDS_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[]{};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new TurinLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        /*if (TurinTokenTypes.KEYWORDS.contains(tokenType)) {
            return KEYWORDS_KEYS;
        } else if (TurinTokenTypes.IDENTIFIERS.contains(tokenType)) {
            return IDENTIFIER_KEYS;
        } else if (TurinTokenTypes.COMMENTS.contains(tokenType)) {
            return COMMENT_KEYS;
        } else {*/
            return new TextAttributesKey[]{DefaultLanguageHighlighterColors.CLASS_NAME};
        //}
    }
}
