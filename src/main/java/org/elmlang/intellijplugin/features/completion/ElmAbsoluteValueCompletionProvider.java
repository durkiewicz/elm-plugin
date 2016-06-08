package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmImportClause;

import java.util.Optional;
import java.util.stream.Stream;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.addStringToResult;

class ElmAbsoluteValueCompletionProvider {
    void addCompletions(ElmFile file, String moduleOrAlias, CompletionResultSet resultSet) {
        String moduleName = file.getImportClauses().stream()
                .filter(e -> importHasAlias(e, moduleOrAlias))
                .findFirst()
                .map(e -> e.getModuleName().getText())
                .orElse(moduleOrAlias);
        ElmModuleIndex.getFilesByModuleName(moduleName, file.getProject())
                .forEach(f -> addCompletionsFromOtherFile(f, resultSet));
    }

    private static boolean importHasAlias(ElmImportClause element, String moduleName) {
        return Optional.ofNullable(element.getAsClause())
                .map(e -> e.getUpperCaseId().getText().equals(moduleName))
                .orElse(false);

    }

    private static void addCompletionsFromOtherFile(ElmFile file, CompletionResultSet resultSet) {
        Stream.concat(
                file.getExposedValues().map(e -> (PsiElement) e),
                file.getExposedTypes().map(e -> (PsiElement) e)
        ).forEach(e -> addStringToResult(e.getText(), resultSet));
    }
}
