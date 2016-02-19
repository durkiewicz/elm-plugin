package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FixedWordsProvider implements Provider {
    private final String[] words;

    public FixedWordsProvider(String... words) {
        this.words = words;
    }

    @Override
    public boolean addCompletions(@NotNull CompletionParameters parameters,
                               @NotNull CompletionResultSet resultSet) {

        resultSet.addAllElements(getLookupElements());
        return false;
    }

    private Iterable<LookupElement> getLookupElements() {
        List<LookupElement> result = new LinkedList<LookupElement>();
        for (String w : this.words) {
            result.add(LookupElementBuilder.create(w));
        }
        return result;
    }
}
