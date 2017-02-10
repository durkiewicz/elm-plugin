package org.elmlang.intellijplugin.psi.references.utils;

import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmTreeUtil;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.scope.ElmCoreLibrary;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class AbsoluteReferencesHelper {
    @Nullable
    public static <T extends PsiElement> PsiElement resolveAbsoluteReference(List<ElmUpperCaseId> moduleName, Function<ElmFile, Optional<T>> resolver) {
        PsiElement psiElement = getModuleNames(moduleName)
                .map(name -> ModuleIndexHelper.resolveUsingModuleIndex(
                        name,
                        moduleName.get(0).getProject(),
                        resolver))
                .filter(e -> e != null)
                .findFirst()
                .orElse(null);
        if (psiElement == null) {
            return null;
        }
        return psiElement;
    }

    private static Stream<String> getModuleNames(List<ElmUpperCaseId> upperCaseIdList) {
        return getModuleNamesAsLists(upperCaseIdList)
                .map(ElmTreeUtil::joinUsingDot)
                .filter(name ->
                        ElmCoreLibrary.isImplicitImport(name)
                                || getContainingFile(upperCaseIdList).getImportClauseByModuleName(name).isPresent()
                );
    }

    private static Stream<List<ElmUpperCaseId>> getModuleNamesAsLists(List<ElmUpperCaseId> upperCaseIdList) {
        return Stream.concat(
                Stream.of(upperCaseIdList),
                getAliasedModules(upperCaseIdList));
    }

    private static Stream<List<ElmUpperCaseId>> getAliasedModules(List<ElmUpperCaseId> upperCaseIdList) {
        if (upperCaseIdList.size() == 1) {
            String alias = upperCaseIdList.get(0).getText();
            return getContainingFile(upperCaseIdList).getImportClausesByAlias(alias)
                    .map(e -> e.getModuleName().getUpperCaseIdList());
        }
        return Stream.empty();
    }

    private static ElmFile getContainingFile(List<ElmUpperCaseId> upperCaseIdList) {
        ElmUpperCaseId elem = upperCaseIdList.get(0);
        return (ElmFile) elem.getContainingFile();
    }
}
