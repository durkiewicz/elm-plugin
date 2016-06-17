package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;

import java.util.Arrays;
import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.*;

class ElmKeywordsCompletionsProvider {
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
            "else",
            "port"
    };

    void addCompletions(CompletionResultSet resultSet) {
        Arrays.stream(KEYWORDS)
                .forEach(s -> addStringToResult(s, resultSet));
    }
}
