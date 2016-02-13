package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class FixedWordsProvider extends CompletionProvider<CompletionParameters> {
    private final String[] words;

    public FixedWordsProvider(String ... words) {
        this.words = words;
    }

    @Override
    public void addCompletions(@NotNull CompletionParameters parameters,
                               ProcessingContext context,
                               @NotNull CompletionResultSet resultSet) {
        resultSet.addAllElements(this.getLookupElements());

    }

    private Iterable<LookupElement> getLookupElements() {
        List<LookupElement> result = new LinkedList<LookupElement>();
        for (String w : this.words) {
            result.add(LookupElementBuilder.create(w));
        }
        return result;
    }
}
