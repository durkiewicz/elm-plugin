package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmImportClause;

import java.util.Optional;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.*;

public class ElmModuleCompletionProvider {
    public void addCompletions(ElmFile file, CompletionResultSet resultSet) {
        addCompletions(file.getProject(), "", resultSet);
        addTypeAliasCompletions(file, resultSet);
    }

    public void addCompletions(Project project, String prefix, CompletionResultSet resultSet) {
        ElmModuleIndex.getAllModuleNames(project).stream()
                .map(s -> getModulePart(s, prefix))
                .forEach(optionalString -> optionalString.ifPresent(s -> addStringToResult(s, resultSet)));
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
