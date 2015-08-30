package me.tomassetti.turin.idea.antlradaptor.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * This interface supports constructing a {@link com.intellij.psi.PsiElement} from an {@link com.intellij.lang.ASTNode}.
 */
public interface PsiElementFactory {
	PsiElement createElement(ASTNode node);
}
