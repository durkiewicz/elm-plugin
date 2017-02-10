package org.elmlang.intellijplugin;

import com.intellij.testFramework.ParsingTestCase;

public class CommentParsingTest extends ParsingTestCase {
    public CommentParsingTest() {
        super("", "elm", new ElmParserDefinition());
    }

    public void testLine() {
        doTest(true);
    }

    public void testBlock() {
        doTest(true);
    }

    public void testDoc() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData/parsing/comments";
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
