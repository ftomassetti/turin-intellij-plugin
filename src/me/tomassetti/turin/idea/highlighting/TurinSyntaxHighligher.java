package me.tomassetti.turin.idea.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.parser.TurinPsiLexer;
import me.tomassetti.turin.idea.parser.TurinTokenTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class TurinSyntaxHighligher extends SyntaxHighlighterBase {

    public static final TextAttributesKey KEYWORD= createTextAttributesKey("TURIN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey VALUE_IDENTIFIER = createTextAttributesKey("TURIN_VALUE_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey TYPE_IDENTIFIER = createTextAttributesKey("TURIN_TYPE_IDENTIFIER", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("TURIN_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey STRING = createTextAttributesKey("TURIN_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER = createTextAttributesKey("TURIN_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey BRACKETS = createTextAttributesKey("TURIN_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);

    private static final TextAttributesKey[] KEYWORDS_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] VALUE_IDENTIFIER_KEYS = new TextAttributesKey[]{VALUE_IDENTIFIER};
    private static final TextAttributesKey[] TYPE_IDENTIFIER_KEYS = new TextAttributesKey[]{TYPE_IDENTIFIER};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] BRACKETS_KEYS = new TextAttributesKey[]{BRACKETS};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[]{};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new TurinPsiLexer(true);
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (TurinTokenTypes.KEYWORDS.contains(tokenType)) {
            return KEYWORDS_KEYS;
        } else if (TurinTokenTypes.VALUE_IDENTIFIERS.contains(tokenType)) {
            return VALUE_IDENTIFIER_KEYS;
        } else if (TurinTokenTypes.TYPE_IDENTIFIERS.contains(tokenType)) {
            return TYPE_IDENTIFIER_KEYS;
        } else if (TurinTokenTypes.COMMENTS.contains(tokenType)) {
            return COMMENT_KEYS;
        } else if (TurinTokenTypes.STRINGS.contains(tokenType)) {
            return STRING_KEYS;
        } else if (TurinTokenTypes.NUMBERS.contains(tokenType)) {
            return NUMBER_KEYS;
        } else if (TurinTokenTypes.BRACKETS.contains(tokenType)) {
            return BRACKETS_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
