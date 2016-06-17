package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.scope.ElmScope;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.addPsiElementToResult;
import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.forEachUntilNonPresent;

class ElmRecordFieldsCompletionProvider {
    void addCompletions(ElmFile file, CompletionResultSet resultSet) {
        forEachUntilNonPresent(
                ElmScope.recordFieldsFor(file),
                e -> addPsiElementToResult(e, resultSet)
        );
    }
}
