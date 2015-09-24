package me.tomassetti.turin.idea.completion;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.util.IncorrectOperationException;
import me.tomassetti.turin.idea.psi.PsiNodeRecognition;
import me.tomassetti.turin.idea.psi.TurinPsiUtils;

/**
* Try to shorten a type reference by using the simple name and adding an import.
*/
class TryShorteningTypeReference implements InsertHandler<JavaPsiClassReferenceElement> {

    private Project project;

    public TryShorteningTypeReference(Project project) {
        this.project = project;
    }

    private void _handleInsert(final InsertionContext context, final JavaPsiClassReferenceElement item) {
        final Editor editor = context.getEditor();
        final PsiClass psiClass = item.getObject();
        if (!psiClass.isValid()) return;

        int endOffset = editor.getCaretModel().getOffset();
        final String qname = psiClass.getQualifiedName();
        if (qname == null) return;

        if (endOffset == 0) return;

        final Document document = editor.getDocument();
        final PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(psiClass.getProject());
        final PsiFile file = context.getFile();
        TurinCompletionContributor.log("file = " + file);
        if (file.findElementAt(endOffset - 1) == null) return;

        final OffsetKey key = OffsetKey.create("endOffset", false);
        TurinCompletionContributor.log("key = " + key);
        context.getOffsetMap().addOffset(key, endOffset);
        PostprocessReformattingAspect.getInstance(context.getProject()).doPostponedFormatting();

        final int newOffset = context.getOffsetMap().getOffset(key);
        TurinCompletionContributor.log("newOffset = " + newOffset);
        if (newOffset >= 0) {
            endOffset = newOffset;
        }
        else {
            // TODO
            // LOG.error(endOffset + " became invalid: " + context.getOffsetMap() + "; inserting " + qname);
        }

        // TODO determine when the import can be actually used and when not
        boolean usingImportAndSimpleName = true;
        if (usingImportAndSimpleName) {
            PsiElement importDecl = new TurinPsiUtils(project).createImportStatement(psiClass.getQualifiedName());
            System.out.println("FEDERICO importDecl = " + importDecl);
            if (importDecl != null) {
                context.getFile().getNode().getChildren(null)[0].addChild(importDecl.getNode(), PsiNodeRecognition.findFirstNodeAfterImports(context.getFile()));
                PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
            }
            new SimpleClassNameInsertHandler(psiClass.getName()).handleInsert(context, item);
            return;
        }

        final RangeMarker toDelete = JavaCompletionUtil.insertTemporary(endOffset, document, " ");
        psiDocumentManager.commitAllDocuments();
        TurinCompletionContributor.log("file = " + file);
        TurinCompletionContributor.log("endOffset = " + endOffset);
        PsiReference psiReference = file.findReferenceAt(endOffset - 1);

        boolean insertFqn = true;
        TurinCompletionContributor.log("psiClass = " + psiClass);
        TurinCompletionContributor.log("psiReference = " + psiReference);
        if (psiReference != null) {
            final PsiManager psiManager = file.getManager();
            if (psiManager.areElementsEquivalent(psiClass, TurinCompletionContributor.resolveReference(psiReference))) {
                insertFqn = false;
            }
            else if (psiClass.isValid()) {
                try {
                    context.setTailOffset(psiReference.getRangeInElement().getEndOffset() + psiReference.getElement().getTextRange().getStartOffset());
                    final PsiElement newUnderlying = psiReference.bindToElement(psiClass);
                    if (newUnderlying != null) {
                        final PsiElement psiElement = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(newUnderlying);
                        if (psiElement != null) {
                            for (final PsiReference reference : psiElement.getReferences()) {
                                if (psiManager.areElementsEquivalent(psiClass, TurinCompletionContributor.resolveReference(reference))) {
                                    insertFqn = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (IncorrectOperationException e) {
                    //if it's empty we just insert fqn below
                }
            }
        }
        if (toDelete.isValid()) {
            document.deleteString(toDelete.getStartOffset(), toDelete.getEndOffset());
            context.setTailOffset(toDelete.getStartOffset());
        }

        if (insertFqn) {
            AllClassesGetter.INSERT_FQN.handleInsert(context, item);
        }
    }

    @Override
    public void handleInsert(final InsertionContext context, final JavaPsiClassReferenceElement item) {
        _handleInsert(context, item);
        item.getTailType().processTail(context.getEditor(), context.getEditor().getCaretModel().getOffset());
    }

}
