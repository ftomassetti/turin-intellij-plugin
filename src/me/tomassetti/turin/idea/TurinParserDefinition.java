package me.tomassetti.turin.idea;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import me.tomassetti.turin.idea.lexer.ExtraTokenTypes;
import me.tomassetti.turin.idea.lexer.MyAwfulLexer;
import me.tomassetti.turin.idea.lexer.MyLexerAdapter;
import me.tomassetti.turin.idea.lexer.TurinLexerAdapter;
import me.tomassetti.turin.idea.parser.TurinIdeaParser;
import me.tomassetti.turin.idea.psi.TurinTypes;
import org.jetbrains.annotations.NotNull;

public class TurinParserDefinition implements ParserDefinition {
    public static final TokenSet COMMENTS = TokenSet.create(ExtraTokenTypes.LINE_COMMENT);
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

    public static final IFileElementType FILE = new IFileElementType(Language.<TurinLanguage>findInstance(TurinLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new TurinLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new TurinIdeaParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new TurinFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return TurinTypes.Factory.createElement(node);
    }

}
