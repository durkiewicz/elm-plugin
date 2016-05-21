package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.elmlang.intellijplugin.ElmLanguage;
import org.jetbrains.annotations.NotNull;

public class ElmCompletionContributor extends CompletionContributor {
    public ElmCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns
                        .psiElement()
                        .withLanguage(ElmLanguage.INSTANCE),
                getProvider()
        );
    }

    private static CompletionProvider<CompletionParameters> getProvider() {
        return new ElmCompletionProvider();
    }
}
