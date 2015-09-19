package me.tomassetti.turin.idea.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.lexer.ExtraTokenTypes;
import me.tomassetti.turin.idea.lexer.MyAwfulLexer;
import me.tomassetti.turin.idea.lexer.MyLexerAdapter;
import me.tomassetti.turin.idea.lexer.TurinLexerAdapter;
import me.tomassetti.turin.idea.psi.TurinTypes;
import org.jetbrains.annotations.NotNull;

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
        return new TurinLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(TurinTypes.ID) || tokenType.equals(TurinTypes.TID)){
            return IDENTIFIER_KEYS;
        } else if (tokenType.equals(TurinTypes.NAMESPACE_KW)||tokenType.equals(TurinTypes.PROPERTY_KW)||tokenType.equals(TurinTypes.TYPE_KW)) {
            return KEYWORDS_KEYS;
        } else if (tokenType.equals(ExtraTokenTypes.LINE_COMMENT)) {
            return COMMENT_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
