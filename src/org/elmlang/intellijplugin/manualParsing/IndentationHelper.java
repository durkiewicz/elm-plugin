package org.elmlang.intellijplugin.manualParsing;


import com.intellij.lang.PsiBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndentationHelper {
    private static final Pattern indentationPattern = Pattern.compile(".*[\r\n]([ \t]+)$", Pattern.DOTALL);

    public static int getIndentationOfPreviousToken(PsiBuilder builder) {
        // getTokenType has some side effects. Do not remove the call.
        builder.getTokenType();
        int offset = -1;
        int start = builder.rawTokenTypeStart(offset);
        int end = builder.rawTokenTypeStart(offset + 1);
        return getIndentation(builder.getOriginalText(), start, end);
    }

    public static int getIndentation(CharSequence text, int start, int end) {
        CharSequence previousChars = text.subSequence(start, end);
        Matcher m = indentationPattern.matcher(previousChars);
        if (m.matches()) {
            return m.group(1).length();
        }
        return 0;
    }
}
