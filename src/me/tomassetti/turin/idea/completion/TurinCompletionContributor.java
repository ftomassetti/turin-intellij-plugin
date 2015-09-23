package me.tomassetti.turin.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import me.tomassetti.parser.antlr.TurinParser;
import me.tomassetti.turin.idea.TurinLanguage;
import me.tomassetti.turin.idea.antlradaptor.lexer.RuleElementType;
import me.tomassetti.turin.idea.antlradaptor.lexer.TokenElementType;
import me.tomassetti.turin.idea.parser.TurinTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


public class TurinCompletionContributor extends CompletionContributor {

    public boolean isNl(ASTNode node) {
        if (node == null) {
            return false;
        }
        if (node.getElementType() == null) {
            return false;
        }
        return node.getElementType().getIndex() == TurinTokenTypes.TOKEN_ELEMENT_TYPES.get(TurinParser.NL).getIndex();
    }

    public boolean isWs(ASTNode node) {
        if (node == null) {
            return false;
        }
        if (node.getElementType() == null) {
            return false;
        }
        return node.getElementType().getIndex() == TurinTokenTypes.TOKEN_ELEMENT_TYPES.get(TurinParser.WS).getIndex();
    }

    public boolean isNlWs(ASTNode node) {
        return isNl(node) || isWs(node);
    }

    public boolean isRule(ASTNode node, int rule) {
        if (node == null) {
            return false;
        }
        if (node.getElementType() == null) {
            return false;
        }
        return node.getElementType().getIndex() == TurinTokenTypes.RULE_ELEMENT_TYPES.get(rule).getIndex();
    }

    private enum Place {
        STATEMENT_CONTAINER, FILE_MEMBER
    }

    private void explore(ASTNode node, int level, TextRange textRange, Set<Place> places) {
        // treat NL or WS in a special way (look for successors nodes)
        if (level == 0 && isNl(node)) {
            ASTNode end = node;
            while (isNlWs(end.getTreeNext())) {
                end = end.getTreeNext();
            }
            textRange = end.getTextRange();
        }

        System.out.println("FEDERICO (level + " + level+") " + node + " starting at " + node.getStartOffset() + " ending at " + node.getTextRange().getEndOffset());
        boolean sameStart = node.getTextRange().getStartOffset() == textRange.getStartOffset();
        boolean sameEnd = node.getTextRange().getEndOffset() == textRange.getEndOffset();
        boolean inside = !sameStart && !sameEnd;
        if (sameStart) {
            System.out.println("  FEDERICO same start");
            if (isRule(node, TurinParser.RULE_fileMember)) {
                places.add(Place.FILE_MEMBER);
            }
        }
        if (sameEnd) {
            System.out.println("  FEDERICO same end");
            if (isRule(node, TurinParser.RULE_fileMember)) {
                places.add(Place.FILE_MEMBER);
            }
        }
        if (inside) {
            System.out.println("  FEDERICO same inside");
            if (isRule(node, TurinParser.RULE_methodBody)) {
                places.add(Place.STATEMENT_CONTAINER);
            }
        }

        IElementType elementType = node.getElementType();
        for (RuleElementType ruleElementType :TurinTokenTypes.RULE_ELEMENT_TYPES) {
            if (ruleElementType.getIndex() == elementType.getIndex()) {
                int ruleIndex = ruleElementType.getRuleIndex();
                System.out.println("  FEDERICO -> it is a RE " + ruleIndex);
            }
        }
        for (TokenElementType ruleElementType :TurinTokenTypes.TOKEN_ELEMENT_TYPES) {
            if (ruleElementType.getIndex() == elementType.getIndex()) {
                int ruleIndex = ruleElementType.getType();
                System.out.println("  FEDERICO -> it is a TE " + ruleIndex);
            }
        }

        if (node.getTreeParent() != null) {
            explore(node.getTreeParent(), level + 1, textRange, places);
        }
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        System.out.println("\n=======================\n");
        System.out.println("FEDERICO ON " + parameters);
        //IElementType elementType = parameters.getPosition().getNode().getElementType();
        IElementType elementType = parameters.getOriginalPosition().getNode().getElementType();
        PsiElement psiElement = parameters.getOriginalPosition().getOriginalElement();
        System.out.println("FEDERICO start at " + psiElement.getTextRange().getStartOffset() + " end at " + psiElement.getTextRange().getEndOffset());
        Set<Place> places = new HashSet<Place>();
        explore(psiElement.getNode(), 0, parameters.getOriginalPosition().getNode().getTextRange(), places);

        if (places.contains(Place.FILE_MEMBER)) {
            result.addElement(new LookupElement() {
                @NotNull
                @Override
                public String getLookupString() {
                    return "type NewType {\n}\n";
                }
            });
            result.addElement(new LookupElement() {
                @NotNull
                @Override
                public String getLookupString() {
                    return "program MyProgram (String[] args) {\n}\n";
                }
            });
        }
        if (places.contains(Place.STATEMENT_CONTAINER)) {
            result.addElement(new LookupElement() {
                @NotNull
                @Override
                public String getLookupString() {
                    return "val a = 0\n";
                }
            });
            result.addElement(new LookupElement() {
                @NotNull
                @Override
                public String getLookupString() {
                    return "if true {\n}\n";
                }
            });
            result.addElement(new LookupElement() {
                @NotNull
                @Override
                public String getLookupString() {
                    return "return 0\n";
                }
            });
            result.addElement(new LookupElement() {
                @NotNull
                @Override
                public String getLookupString() {
                    return "try {\n" +
                            "} catch Exception e {\n" +
                            "}\n";
                }
            });
        }
    }

    public TurinCompletionContributor() {
        System.out.println("FEDERICO CONSTRUCTOR");
        /*IElementType elementType = TurinTokenTypes.RULE_ELEMENT_TYPES.get(TurinParser.RULE_fileMember);
        System.out.println("COMPLETEFOR " + elementType.toString());
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(TurinTokenTypes.TOKEN_ELEMENT_TYPES.get(TurinParser.VOID_KW)).withLanguage(TurinLanguage.getInstance()),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("Hello"));
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(TurinTokenTypes.RULE_ELEMENT_TYPES.get(TurinParser.RULE_fileMember)).withLanguage(TurinLanguage.getInstance()),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("type NewType {\n}\n"));
                        resultSet.addElement(LookupElementBuilder.create("program MyProgram (String[] args) {\n}\n"));
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(TurinTokenTypes.RULE_ELEMENT_TYPES.get(TurinParser.RULE_returnType)).withLanguage(TurinLanguage.getInstance()),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("Void"));
                        for (PrimitiveTypeUsage primitiveTypeUsage : PrimitiveTypeUsage.ALL) {
                            resultSet.addElement(LookupElementBuilder.create(primitiveTypeUsage.turinName()));
                        }
                    }
                }
        );*/
    }
}
