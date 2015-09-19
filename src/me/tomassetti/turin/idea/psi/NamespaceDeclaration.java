package me.tomassetti.turin.idea.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import me.tomassetti.turin.idea.antlradaptor.parser.PsiElementFactory;

/**
 * Created by federico on 19/09/15.
 */
public class NamespaceDeclaration extends ASTWrapperPsiElement {

    public NamespaceDeclaration(ASTNode node) {
        super(node);
    }

    public static class Factory implements PsiElementFactory {
        public static Factory INSTANCE = new Factory();

        @Override
        public PsiElement createElement(ASTNode node) {
            return new NamespaceDeclaration(node);
        }
    }
}
