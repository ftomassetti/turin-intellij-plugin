package me.tomassetti.turin.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import me.tomassetti.parser.antlr.TurinParser;
import me.tomassetti.turin.idea.TurinLanguage;
import me.tomassetti.turin.idea.antlradaptor.lexer.RuleElementType;
import me.tomassetti.turin.idea.antlradaptor.lexer.TokenElementType;
import me.tomassetti.turin.idea.parser.TurinTokenTypes;
import me.tomassetti.turin.implicit.BasicTypeUsage;
import me.tomassetti.turin.parser.ast.typeusage.PrimitiveTypeUsage;
import org.jetbrains.annotations.NotNull;


public class TurinCompletionContributor extends CompletionContributor {
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        System.out.println("FEDERICO ON " + parameters);
        //IElementType elementType = parameters.getPosition().getNode().getElementType();
        IElementType elementType = parameters.getOriginalPosition().getNode().getElementType();
        PsiElement psiElement = parameters.getOriginalPosition().getOriginalElement();
        System.out.println("FEDERICO ON1 " + elementType);
        System.out.println("FEDERICO ON2 " + elementType.getIndex());
        System.out.println("FEDERICO ONPSI " + psiElement);
        System.out.println("FEDERICO ONPSI2 " + psiElement.getNode());
        System.out.println("FEDERICO ONPSI2a " + psiElement.getNode().getTreeParent());
        System.out.println("FEDERICO ONPSI2b " + psiElement.getNode().getTreeParent().getTreeParent());
        System.out.println("FEDERICO ONPSI3 " + psiElement.getText());
        for (RuleElementType ruleElementType :TurinTokenTypes.RULE_ELEMENT_TYPES) {
            if (ruleElementType.getIndex() == elementType.getIndex()) {
                int ruleIndex = ruleElementType.getRuleIndex();
                System.out.println("FEDERICO ON3 " + ruleIndex);
            }
        }
        for (TokenElementType ruleElementType :TurinTokenTypes.TOKEN_ELEMENT_TYPES) {
            if (ruleElementType.getIndex() == elementType.getIndex()) {
                int ruleIndex = ruleElementType.getType();
                System.out.println("FEDERICO ON4 " + ruleIndex);
            }
        }
        result.addElement(new LookupElement() {
            @NotNull
            @Override
            public String getLookupString() {
                return "A";
            }
        });
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
