package me.tomassetti.turin.idea;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import me.tomassetti.turin.idea.parser.TurinAstFactory;
import me.tomassetti.turin.idea.parser.TurinPsiLexer;
import me.tomassetti.turin.idea.parser.TurinTokenTypes;
import org.jetbrains.annotations.NotNull;

public class TurinParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(Language.<TurinLanguage>findInstance(TurinLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new TurinPsiLexer(true);
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return TurinTokenTypes.WHITESPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return TurinTokenTypes.COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new TurinPsiParser();
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
        return TurinAstFactory.createInternalParseTreeNode(node);
    }

}
