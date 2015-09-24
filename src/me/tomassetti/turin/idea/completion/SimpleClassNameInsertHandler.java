package me.tomassetti.turin.idea.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.JavaPsiClassReferenceElement;

/**
 * Insert a simple class name (not qualified).
 */
class SimpleClassNameInsertHandler implements InsertHandler<JavaPsiClassReferenceElement> {

    private String name;
    public SimpleClassNameInsertHandler(String name) {
        this.name = name;
    }

    @Override
    public void handleInsert(InsertionContext context, JavaPsiClassReferenceElement item) {
        int start = context.getTailOffset() - 1;
        while (start >= 0) {
            final char ch = context.getDocument().getCharsSequence().charAt(start);
            if (!Character.isJavaIdentifierPart(ch) && ch != '.') break;
            start--;
        }
        context.getDocument().replaceString(start + 1, context.getTailOffset(), name);
    }
}
