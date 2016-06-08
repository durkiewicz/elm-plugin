package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmImportClause;

import java.util.Optional;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.*;

class ElmModuleCompletionProvider {
    void addCompletions(ElmFile file, CompletionResultSet resultSet) {
        addCompletions(file.getProject(), "", resultSet);
        addTypeAliasCompletions(file, resultSet);
    }

    void addCompletions(Project project, String prefix, CompletionResultSet resultSet) {
        String matcherPrefix = resultSet.getPrefixMatcher().getPrefix();
        int dotIndex = matcherPrefix.lastIndexOf('.');
        CompletionResultSet newResultSet = dotIndex < 0
                ? resultSet
                : resultSet.withPrefixMatcher(matcherPrefix.substring(dotIndex + 1));

        ElmModuleIndex.getAllModuleNames(project).stream()
                .map(s -> getModulePart(s, prefix))
                .forEach(optionalString -> optionalString.ifPresent(s -> addStringToResult(s, newResultSet)));
    }

    private static void addTypeAliasCompletions(ElmFile file, CompletionResultSet resultSet) {
        file.getImportClauses().stream()
                .map(ElmImportClause::getAsClause)
                .filter(e -> e != null)
                .forEach(e -> addPsiElementToResult(e.getUpperCaseId(), resultSet));
    }

    private static Optional<String> getModulePart(String moduleName, String prefix) {
        if (moduleName.length() <= prefix.length() || !moduleName.startsWith(prefix)) {
            return Optional.empty();
        }

        String substring = prefix.length() == 0 ? moduleName : moduleName.substring(prefix.length() + 1);
        int dotIndex = substring.indexOf('.');
        return Optional.of(dotIndex < 0 ? substring : substring.substring(0, dotIndex));
    }
}
