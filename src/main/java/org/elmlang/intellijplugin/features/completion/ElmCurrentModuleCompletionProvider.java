package org.elmlang.intellijplugin.features.completion;

import com.google.common.base.Joiner;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.utils.OptionalUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.addStringToResult;

class ElmCurrentModuleCompletionProvider {
    private static final String ELM_EXTENSION = ".elm";
    private static final char DOT = '.';

    void addCompletions(ElmFile file, CompletionResultSet resultSet) {
        Optional<String> filePath = Optional.ofNullable(file.getOriginalFile().getVirtualFile())
                .map(VirtualFile::getPath);
        Optional<String> projectPath = Optional.ofNullable(file.getProject().getBasePath());

        OptionalUtils.map2(filePath, projectPath, Pair::create)
                .ifPresent(p -> addCompletions(p, resultSet));
    }

    private void addCompletions(Pair<String, String> paths, CompletionResultSet resultSet) {
        String filePath = paths.first;
        String projectPath = paths.second;
        if (!filePath.startsWith(projectPath) || !filePath.endsWith(ELM_EXTENSION)) {
            return;
        }

        List<String> relativePath = new LinkedList<>(
                Arrays.asList(
                        filePath.substring(projectPath.length() + 1, filePath.indexOf(ELM_EXTENSION))
                                .split(Pattern.quote(File.separator))
                )
        );

        while (relativePath.size() > 0) {
            String elem = relativePath.get(0);
            if (Character.isUpperCase(elem.charAt(0))) {
                break;
            } else {
                relativePath.remove(0);
            }
        }

        while (relativePath.size() > 0) {
            addStringToResult(Joiner.on(DOT).join(relativePath), resultSet);
            relativePath.remove(0);
        }
    }
}
