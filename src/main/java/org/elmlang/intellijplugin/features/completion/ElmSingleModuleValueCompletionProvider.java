package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import org.elmlang.intellijplugin.psi.ElmFile;

import java.util.stream.Stream;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.addPsiElementToResult;

class ElmSingleModuleValueCompletionProvider {
    void addCompletions(ElmFile element, CompletionResultSet resultSet) {
        Stream.concat(
                element.getAllDefinedValues(),
                element.getInternalTypes())
                .forEach(id -> addPsiElementToResult(id, resultSet));
    }
}
