package me.tomassetti.turin.idea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiFile;
import me.tomassetti.parser.antlr.TurinParser;
import me.tomassetti.turin.idea.parser.TurinTokenTypes;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by federico on 24/09/15.
 */
public class PsiNodeRecognition {

    public static boolean isNl(ASTNode node) {
        if (node == null) {
            return false;
        }
        if (node.getElementType() == null) {
            return false;
        }
        return node.getElementType().getIndex() == TurinTokenTypes.TOKEN_ELEMENT_TYPES.get(TurinParser.NL).getIndex();
    }

    public static boolean isNls(ASTNode node) {
        return isRule(node, TurinParser.RULE_nls);
    }

    public static boolean isWs(ASTNode node) {
        if (node == null) {
            return false;
        }
        if (node.getElementType() == null) {
            return false;
        }
        return node.getElementType().getIndex() == TurinTokenTypes.TOKEN_ELEMENT_TYPES.get(TurinParser.WS).getIndex();
    }

    public static boolean isNlWs(ASTNode node) {
        return isNl(node) || isWs(node);
    }

    public static boolean isRule(ASTNode node, int rule) {
        if (node == null) {
            return false;
        }
        if (node.getElementType() == null) {
            return false;
        }
        return node.getElementType().getIndex() == TurinTokenTypes.RULE_ELEMENT_TYPES.get(rule).getIndex();
    }

    public static boolean isImport(ASTNode node) {
        return isRule(node, TurinParser.RULE_importDeclaration);
    }

    public static Optional<ASTNode> findImport(ASTNode node) {
        if (isImport(node)) {
            return Optional.of(node);
        }
        for (ASTNode child : node.getChildren(null)){
            Optional<ASTNode> partial = findImport(child);
            if (partial.isPresent()) {
                return partial;
            }
        }
        return Optional.empty();
    }

    public static ASTNode findFirstNodeAfterImports(PsiFile file) {
        boolean namespaceSkipped = false;
        System.out.println("FEDERICO findFirstNodeAfterImports children "+ Arrays.toString(file.getNode().getChildren(null)[0].getChildren(null)));
        for (ASTNode child : file.getNode().getChildren(null)[0].getChildren(null)){
            if (!namespaceSkipped) {
                namespaceSkipped = true;
            } else {
                System.out.println("FEDERICO findFirstNodeAfterImports children look at "+ child);
                if (!isImport(child) && !isNls(child)) {
                    return child;
                }
            }
        }
        return null;
    }
}
