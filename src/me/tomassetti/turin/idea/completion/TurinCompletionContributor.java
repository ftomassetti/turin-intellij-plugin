package me.tomassetti.turin.idea.completion;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Consumer;
import com.intellij.util.IncorrectOperationException;
import me.tomassetti.parser.antlr.TurinParser;
import me.tomassetti.turin.idea.antlradaptor.lexer.RuleElementType;
import me.tomassetti.turin.idea.antlradaptor.lexer.TokenElementType;
import me.tomassetti.turin.idea.psi.PsiNodeRecognition;
import me.tomassetti.turin.idea.psi.TurinPsiUtils;
import me.tomassetti.turin.idea.parser.TurinTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.tomassetti.turin.idea.psi.PsiNodeRecognition.*;

public class TurinCompletionContributor extends CompletionContributor {


    private enum Place {
        STATEMENT_CONTAINER, FILE_MEMBER, TYPE_USAGE
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
            if (isRule(node, TurinParser.RULE_typeUsage)) {
                places.add(Place.TYPE_USAGE);
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

    void grow(List<Module> modules, Module thisModule) {
        modules.add(thisModule);
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(thisModule);
        for (Module m : moduleRootManager.getDependencies()){
            grow(modules, m);
        }
    }

    Collection<PsiFileSystemItem> getRoots(Module thisModule, boolean includingClasses) {
        if (thisModule == null) {
            return Collections.emptyList();
        }
        List<Module> modules = new ArrayList<Module>();
        grow(modules, thisModule);
        List<PsiFileSystemItem> result = new ArrayList<PsiFileSystemItem>();
        PsiManager psiManager = PsiManager.getInstance(thisModule.getProject());
        if (includingClasses) {
            //OrderEntry[] entries = moduleRootManager.getOrderEntries();
            //VirtualFile[] libraryUrls = moduleRootManager.getOrderEntries();
            /*for (file <- libraryUrls) {
                val directory: PsiDirectory = psiManager.findDirectory(file)
                if (directory != null) {
                    result.add(directory)
                }
            }*/
        }
        //LibraryDependencyDataService.EP_NAME
        for (Module module : modules) {
            System.out.println("MODULE " + module);
            ModuleRootManager mrm = ModuleRootManager.getInstance(module);
            VirtualFile[] sourceRoots = mrm.getSourceRoots();
            for (VirtualFile virtualFile : sourceRoots) {
                System.out.println("virtualFile " + virtualFile);
            }
            //module.getModuleWithLibrariesScope()
        }
        /*for (module <- JavaConversions.iterableAsScalaIterable(modules)) {
            moduleRootManager = ModuleRootManager.getInstance(module)
            val sourceRoots: Array[VirtualFile] = moduleRootManager.getSourceRoots
            for (root <- sourceRoots) {
                val directory: PsiDirectory = psiManager.findDirectory(root)
                if (directory != null) {
                    val aPackage: PsiPackage = JavaDirectoryService.getInstance.getPackage(directory)
                    if (aPackage != null && aPackage.name != null) {
                        try {
                            val createMethod = Class.forName("com.intellij.psi.impl.source.resolve.reference.impl.providers.PackagePrefixFileSystemItemImpl").getMethod("create", classOf[PsiDirectory])
                            createMethod.setAccessible(true)
                            createMethod.invoke(directory)
                        } catch {
                            case t: Exception  => LOG.warn(t)
                        }
                    }
                    else {
                        result.add(directory)
                    }
                }
            }
        }*/
        return result;
    }

    @Nullable
    static PsiElement resolveReference(final PsiReference psiReference) {
        if (psiReference instanceof PsiPolyVariantReference) {
            final ResolveResult[] results = ((PsiPolyVariantReference)psiReference).multiResolve(true);
            if (results.length == 1) return results[0].getElement();
        }
        return psiReference.resolve();
    }

    private static void log(String msg) {
        System.out.println("FEDERICO " + msg);
    }

    //public static final InsertHandler<JavaPsiClassReferenceElement> TRY_SHORTENING_IN_TURIN = new JavaPsiClassReferenceElementInsertHandler();

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull final CompletionResultSet result) {

        //AllClassesGetter.processJavaClasses();
                //JavaLookupElementBuilder

        System.out.println("\n=======================\n");
        System.out.println("FEDERICO ON " + parameters);

        Module module = ModuleUtilCore.findModuleForPsiElement(parameters.getOriginalPosition());
        final Project project = module.getProject();

        System.out.println("FEDERICO ON " + project);
        System.out.println("FEDERICO ON " + project.getClass());
        System.out.println("FEDERICO ON " + module);
        System.out.println("FEDERICO ON " + module.getClass());
        getRoots(module, true);

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
        if (places.contains(Place.TYPE_USAGE)) {
            AllClassesGetter.processJavaClasses(parameters, result.getPrefixMatcher(), parameters.getInvocationCount() <= 1, new Consumer<PsiClass>() {
                @Override
                public void consume(PsiClass psiClass) {
                    result.addElement(createClassLookupItem(psiClass, project));
                }
            });
            //ReferenceProvidersRegistry.getInstance().getRegistrar(JavaLanguage.INSTANCE).registerReferenceProvider();
            //JpsArtifactService.getInstance().getSortedArtifacts()
            //JpsArtifactService.getInstance().getArtifacts(JpsProject)
            //PsiReferenceService.getService().getReferences()
            //System.out.println("RES1 " + ReferenceProvidersRegistry.getReferencesFromProviders(new PsiIdentifierImpl("")));
            //System.out.println("RES2 " + ReferenceProvidersRegistry.getReferencesFromProviders(new PsiIdentifierImpl("A")));
        }
    }

    public static JavaPsiClassReferenceElement createClassLookupItem(final PsiClass psiClass, Project project) {
        return AllClassesGetter.createLookupItem(psiClass, new TryShorteningTypeReference(project));
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

    private static class TryShorteningTypeReference implements InsertHandler<JavaPsiClassReferenceElement> {

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
            log("file = " + file);
            if (file.findElementAt(endOffset - 1) == null) return;

            final OffsetKey key = OffsetKey.create("endOffset", false);
            log("key = " + key);
            context.getOffsetMap().addOffset(key, endOffset);
            PostprocessReformattingAspect.getInstance(context.getProject()).doPostponedFormatting();

            final int newOffset = context.getOffsetMap().getOffset(key);
            log("newOffset = " + newOffset);
            if (newOffset >= 0) {
                endOffset = newOffset;
            }
            else {
                //LOG.error(endOffset + " became invalid: " + context.getOffsetMap() + "; inserting " + qname);
            }

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
                //context.getDocument().replaceString(0, 0, "HEY "+psiClass.getQualifiedName());
            }

            final RangeMarker toDelete = JavaCompletionUtil.insertTemporary(endOffset, document, " ");
            psiDocumentManager.commitAllDocuments();
            log("file = " + file);
            log("endOffset = " + endOffset);
            PsiReference psiReference = file.findReferenceAt(endOffset - 1);

            boolean insertFqn = true;
            log("psiClass = " + psiClass);
            log("psiReference = " + psiReference);
            if (psiReference != null) {
                final PsiManager psiManager = file.getManager();
                if (psiManager.areElementsEquivalent(psiClass, resolveReference(psiReference))) {
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
                                    if (psiManager.areElementsEquivalent(psiClass, resolveReference(reference))) {
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

    private static class SimpleClassNameInsertHandler implements InsertHandler<JavaPsiClassReferenceElement> {

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
}
