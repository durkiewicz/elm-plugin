package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;

import java.util.Arrays;
import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.*;

public class ElmKeywordsCompletionsProvider {
    private static final String[] KEYWORDS = new String[] {
            "module",
            "where",
            "import",
            "as",
            "exposing",
            "type",
            "alias",
            "case",
            "of",
            "let",
            "in",
            "infix",
            "infixl",
            "infixr",
            "if",
            "then",
            "else"
    };

    public void addCompletions(CompletionResultSet resultSet) {
        Arrays.stream(KEYWORDS)
                .forEach(s -> addStringToResult(s, resultSet));
    }
}
