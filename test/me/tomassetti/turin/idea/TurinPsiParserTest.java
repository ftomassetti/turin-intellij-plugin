package me.tomassetti.turin.idea;

import com.intellij.testFramework.ParsingTestCase;

public class TurinPsiParserTest extends ParsingTestCase {

    public TurinPsiParserTest() {
        super("", "to", new TurinParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "../turin-intellij-plugin/test_data/simplest";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

}
