package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.Consumer;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.scope.ElmScope;

import java.util.Optional;
import java.util.stream.Stream;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.*;

class ElmValueCompletionProvider {
    void addCompletions(ElmLowerCaseId element, CompletionResultSet resultSet) {
        addCompletions(ElmScope.scopeFor(element), resultSet);
    }

    void addCompletions(ElmFile element, CompletionResultSet resultSet) {
        addCompletions(ElmScope.scopeFor(element), resultSet);
    }

    private void addCompletions(Stream<Optional<ElmLowerCaseId>> stream, CompletionResultSet resultSet) {
        forEachUntilNonPresent(
                stream,
                id -> addPsiElementToResult(id, resultSet)
        );
    }
}
