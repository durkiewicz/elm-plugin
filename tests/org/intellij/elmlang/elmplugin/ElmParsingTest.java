package org.intellij.elmlang.elmplugin;

import com.intellij.testFramework.ParsingTestCase;

public class ElmParsingTest extends ParsingTestCase {
    public ElmParsingTest() {
        super("", "elm", new ElmParserDefinition());
    }

    public void testModuleSimplest() {
        doTest(true);
    }

    public void testModuleExposingAll() {
        doTest(true);
    }

    public void testModuleExposingSomeMembers() {
        doTest(true);
    }

    public void testModulePathName() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
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