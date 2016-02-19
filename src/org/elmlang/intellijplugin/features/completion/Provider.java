package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import org.jetbrains.annotations.NotNull;

public interface Provider {
    boolean addCompletions(@NotNull CompletionParameters parameters,
                           @NotNull CompletionResultSet resultSet);
}
