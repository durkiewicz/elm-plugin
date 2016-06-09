package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import org.elmlang.intellijplugin.ElmLanguage;

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
        return new ElmMainCompletionProvider(
                new ElmValueCompletionProvider(),
                new ElmKeywordsCompletionsProvider(),
                new ElmTypeCompletionProvider(),
                new ElmModuleCompletionProvider(),
                new ElmAbsoluteValueCompletionProvider(),
                new ElmCurrentModuleCompletionProvider());
    }
}
