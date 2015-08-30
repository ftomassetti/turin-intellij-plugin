package me.tomassetti.turin.idea;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import me.tomassetti.turin.idea.antlradaptor.parser.AntlrParser;
import me.tomassetti.turin.idea.antlradaptor.parser.SyntaxErrorListener;
import me.tomassetti.turin.idea.parser.TurinAstFactory;
import me.tomassetti.turin.idea.parser.TurinLexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

public class TurinParserDefinition implements ParserDefinition {
    public static final TokenSet COMMENTS = TokenSet.create();
    public static final TokenSet WHITE_SPACES = TokenSet.create();

    public static final IFileElementType FILE = new IFileElementType(Language.<TurinLanguage>findInstance(TurinLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new TurinLexer();
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
        return new AntlrParser<me.tomassetti.parser.antlr.TurinParser>(TurinLanguage.INSTANCE) {


            @Override
            protected me.tomassetti.parser.antlr.TurinParser createParserImpl(TokenStream tokenStream, IElementType root, PsiBuilder builder) {
                me.tomassetti.parser.antlr.TurinParser turinParser = new me.tomassetti.parser.antlr.TurinParser(tokenStream);
                turinParser.removeErrorListeners();
                turinParser.addErrorListener(new SyntaxErrorListener());
                return  turinParser;
            }

            @Override
            protected ParseTree parseImpl(me.tomassetti.parser.antlr.TurinParser parser, IElementType root, PsiBuilder builder) {
                return parser.turinFile();
            }
        };
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
