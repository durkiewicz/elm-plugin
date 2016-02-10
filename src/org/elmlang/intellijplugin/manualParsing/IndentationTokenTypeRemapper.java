package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.ITokenTypeRemapper;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elmlang.intellijplugin.psi.ElmTypes;

public class IndentationTokenTypeRemapper implements ITokenTypeRemapper {
    private static final Pattern indentationPattern = Pattern.compile(".*[\r\n]([ \t]+)$", Pattern.DOTALL);
    private final int indentation;

    public IndentationTokenTypeRemapper(PsiBuilder builder) {
        // getTokenType has some side effects. Do not remove the call.
        builder.getTokenType();
        int offset = -1;
        int start = builder.rawTokenTypeStart(offset);
        int end = builder.rawTokenTypeStart(offset + 1);
        this.indentation = getIndentation(builder.getOriginalText(), start, end);
    }

    @Override
    public IElementType filter(IElementType type, int start, int end, CharSequence text) {
        if (this.indentation > 0 && TokenType.WHITE_SPACE.equals(type)) {
            int i = getIndentation(text, start, end);
            if (this.indentation == i) {
                return ElmTypes.CASE_OF_SEPARATION;
            }
        }
        return type;
    }

    private static int getIndentation(CharSequence text, int start, int end) {
        CharSequence previousChars = text.subSequence(start, end);
        Matcher m = indentationPattern.matcher(previousChars);
        if (m.matches()) {
            return m.group(1).length();
        }
        return 0;
    }
}
