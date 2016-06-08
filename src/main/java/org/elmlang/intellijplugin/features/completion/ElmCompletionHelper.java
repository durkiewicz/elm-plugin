package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.Consumer;

import java.util.Optional;
import java.util.stream.Stream;

class ElmCompletionHelper {
    static void addPsiElementToResult(PsiElement element, CompletionResultSet resultSet) {
        addStringToResult(element.getText(), resultSet);
    }

    static void addStringToResult(String string, CompletionResultSet resultSet) {
        resultSet.addElement(LookupElementBuilder.create(string));
    }

    static <T> void forEachUntilNonPresent(Stream<Optional<T>> stream, Consumer<T> consumer) {
        stream.peek(e -> e.ifPresent(consumer::consume))
                .filter(e -> !e.isPresent())
                .findFirst();
    }
}
