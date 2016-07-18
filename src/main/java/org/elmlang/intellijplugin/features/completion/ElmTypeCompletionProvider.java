package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.scope.ElmCoreLibrary;
import org.elmlang.intellijplugin.psi.scope.ElmScope;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.*;

class ElmTypeCompletionProvider {
    void addCompletions(ElmFile file, CompletionResultSet resultSet) {
        ElmCoreLibrary.getBuiltInSymbols()
                .forEach(s -> addStringToResult(s, resultSet));
        forEachUntilNonPresent(
                ElmScope.typesFor(file),
                e -> addPsiElementToResult(e, resultSet)
        );
    }
}
