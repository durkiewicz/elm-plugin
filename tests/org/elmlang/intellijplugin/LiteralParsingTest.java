package org.elmlang.intellijplugin;

import com.intellij.testFramework.ParsingTestCase;

public class LiteralParsingTest extends ParsingTestCase {
    public LiteralParsingTest() {
        super("", "elm", new ElmParserDefinition());
    }

    public void testEmptyString() {
        doTest(true);
    }

    public void testFloat() {
        doTest(true);
    }

    public void testInt() {
        doTest(true);
    }

    public void testLongInt() {
        doTest(true);
    }

    public void testMultiLineString() {
        doTest(true);
    }

    public void testQuotedString() {
        doTest(true);
    }

    public void testStrangeFloat() {
        doTest(true);
    }

    public void testString() {
        doTest(true);
    }

    public void testChar() {
        doTest(true);
    }

    public void testEscapedChar() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/parsing/literals";
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
