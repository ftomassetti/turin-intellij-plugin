package me.tomassetti.turin.idea.psi;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import me.tomassetti.turin.idea.TurinFileType;
import me.tomassetti.turin.parser.ast.TurinFile;
import me.tomassetti.turin.parser.ast.imports.ImportDeclaration;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by federico on 24/09/15.
 */
public class TurinPsiUtils {

    private PsiManager myManager;

    public TurinPsiUtils(PsiManager myManager) {
        this.myManager = myManager;
    }

    public TurinPsiUtils(Project project) {
        this.myManager = PsiManager.getInstance(project);
    }

    private static final String DUMMY_FILE_NAME = "_Dummy_." + TurinFileType.INSTANCE.getDefaultExtension();

    @NotNull
    public PsiElement createImportStatement(String qualifiedName) throws IncorrectOperationException {
        final PsiFile aFile = createDummyTurinFile("namespace foo\nimport " + qualifiedName + "\n");
        final PsiElement statement = extractImport(aFile);
        if (statement != null) {
            return CodeStyleManager.getInstance(myManager.getProject()).reformat(statement);
        } else {
            return null;
        }
    }

    protected PsiFile createDummyTurinFile(@NonNls final String text) {
        final FileType type = TurinFileType.INSTANCE;
        return PsiFileFactory.getInstance(myManager.getProject()).createFileFromText(DUMMY_FILE_NAME, type, text);
    }

    private static PsiElement extractImport(final PsiFile aFile) {
        Optional<ASTNode> importNode = PsiNodeRecognition.findImport(aFile.getNode());
        if (importNode.isPresent()) {
            return importNode.get().getPsi();
        } else {
            return null;
        }
        /*final PsiImportList importList = aFile.getImportList();
        assert importList != null : aFile;
        final PsiImportStatementBase[] statements = isStatic ? importList.getImportStaticStatements() : importList.getImportStatements();
        assert statements.length == 1 : aFile.getText();
        return statements[0];*/
    }
}
