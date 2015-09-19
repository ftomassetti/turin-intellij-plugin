package me.tomassetti.turin.idea;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.junit.Test;
import com.intellij.testFramework.ParsingTestCase;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtil;

import java.io.File;

public class TurinPsiParserTest extends ParsingTestCase {

    public TurinPsiParserTest() {
        super("parserData", "to", new TurinParserDefinition());
    }

    @Test
    public void parseSimplestFile() {
        TurinPsiParser instance = new TurinPsiParser();
        IElementType root = null;
        PsiBuilder psiBuilder = null;
        ASTNode result = instance.parse(root, psiBuilder);
    }

    @Override
    protected String getTestDataPath() {
        return TEST_DATA_PATH;
    }

    @Test
    public void testCodeBlock() {
        doTest(true);
    }

    public static final String TEST_DATA_PATH = findTestDataPath();

    private static String findTestDataPath() {
        if (new File(PathManager.getHomePath() + "/contrib").isDirectory()) {
            return FileUtil.toSystemIndependentName(PathManager.getHomePath() + "/contrib/turin/test/data");
        }

        final String parentPath = PathUtil.getParentPath(PathManager.getHomePath());

        if (new File(parentPath + "/intellij-plugins").isDirectory()) {
            return FileUtil.toSystemIndependentName(parentPath + "/intellij-plugins/turin/test/data");
        }

        if (new File(parentPath + "/contrib").isDirectory()) {
            return FileUtil.toSystemIndependentName(parentPath + "/contrib/turin/test/data");
        }

        return "";
    }

}
