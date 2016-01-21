package org.intellij.elmlang.elmplugin;

import com.intellij.testFramework.ParsingTestCase;

public class ImportParsingTest extends ParsingTestCase {
    public ImportParsingTest() {
        super("", "elm", new ElmParserDefinition());
    }

    public void testSimplest() {
        doTest(true);
    }

    public void testAlias() {
        doTest(true);
    }

    public void testExposingAll() {
        doTest(true);
    }

    public void testExposingSomeMembers() {
        doTest(true);
    }

    public void testPathName() {
        doTest(true);
    }

    public void testBrokenLines() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/import";
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