package me.tomassetti.turin.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Consumer;
import me.tomassetti.parser.antlr.TurinParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static me.tomassetti.turin.idea.psi.PsiNodeRecognition.*;

/**
 * This is responsible for adding new options to the auto-completion.
 */
public class TurinCompletionContributor extends CompletionContributor {

    /**
     * A position in the code can represent several type of location.
     * Each type is represented by one entry.
     */
    private enum Place {
        /**
         * Inside a statement container but not inside a statement.
         */
        STATEMENT_CONTAINER,

        /**
         * At the beginning or the end of a file member (the top level declarations)
         */
        FILE_MEMBER,

        /**
         * At the beginning of a type usafe.
         */
        TYPE_USAGE
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

        log("(level + " + level+") " + node + " starting at " + node.getStartOffset() + " ending at " + node.getTextRange().getEndOffset());
        boolean sameStart = node.getTextRange().getStartOffset() == textRange.getStartOffset();
        boolean sameEnd = node.getTextRange().getEndOffset() == textRange.getEndOffset();
        boolean inside = !sameStart && !sameEnd;
        if (sameStart) {
            log("  same start");
            if (isRule(node, TurinParser.RULE_fileMember)) {
                places.add(Place.FILE_MEMBER);
            }
            if (isRule(node, TurinParser.RULE_typeUsage)) {
                places.add(Place.TYPE_USAGE);
            }
        }
        if (sameEnd) {
            log("  same end");
            if (isRule(node, TurinParser.RULE_fileMember)) {
                places.add(Place.FILE_MEMBER);
            }
        }
        if (inside) {
            log("  same inside");
            if (isRule(node, TurinParser.RULE_methodBody)) {
                places.add(Place.STATEMENT_CONTAINER);
            }
        }

        if (node.getTreeParent() != null) {
            explore(node.getTreeParent(), level + 1, textRange, places);
        }
    }

    @Nullable
    static PsiElement resolveReference(final PsiReference psiReference) {
        if (psiReference instanceof PsiPolyVariantReference) {
            final ResolveResult[] results = ((PsiPolyVariantReference)psiReference).multiResolve(true);
            if (results.length == 1) return results[0].getElement();
        }
        return psiReference.resolve();
    }

    public static void log(String msg) {
        System.out.println("FEDERICO " + msg);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull final CompletionResultSet result) {
        Module module = ModuleUtilCore.findModuleForPsiElement(parameters.getOriginalPosition());
        final Project project = module.getProject();

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
        if (places.contains(Place.TYPE_USAGE)) {
            AllClassesGetter.processJavaClasses(parameters, result.getPrefixMatcher(), parameters.getInvocationCount() <= 1, new Consumer<PsiClass>() {
                @Override
                public void consume(PsiClass psiClass) {
                    result.addElement(createClassLookupItem(psiClass, project));
                }
            });
        }
    }

    public static JavaPsiClassReferenceElement createClassLookupItem(final PsiClass psiClass, Project project) {
        return AllClassesGetter.createLookupItem(psiClass, new TryShorteningTypeReference(project));
    }

}
