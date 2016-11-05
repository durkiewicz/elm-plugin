package org.elmlang.intellijplugin.psi.references.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;


public class ModuleIndexHelper {
    @Nullable
    public static <T extends PsiElement> PsiElement resolveUsingModuleIndex(String moduleName, Project project, Function<ElmFile, Optional<T>> resolver) {
        return ElmModuleIndex.getFilesByModuleName(moduleName, project).stream()
                .map(resolver)
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty())
                .orElse(null);
    }
}
