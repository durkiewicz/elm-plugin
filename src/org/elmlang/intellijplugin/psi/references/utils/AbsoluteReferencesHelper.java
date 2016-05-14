package org.elmlang.intellijplugin.psi.references.utils;

import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;

import java.util.List;
import java.util.stream.Collectors;

public class AbsoluteReferencesHelper {
    public static String getModuleName(List<ElmUpperCaseId> upperCaseIdList) {
        return joinPath(getModuleNameAsList(upperCaseIdList));
    }

    private static String joinPath(List<ElmUpperCaseId> path) {
        return path.stream()
                .map(PsiElement::getText)
                .collect(Collectors.joining("."));
    }

    private static List<ElmUpperCaseId> getModuleNameAsList(List<ElmUpperCaseId> upperCaseIdList) {
        if (upperCaseIdList.size() == 1) {
            ElmUpperCaseId elem = upperCaseIdList.get(0);
            ElmFile file = (ElmFile) elem.getContainingFile();
            return file.getImportClauseByAlias(elem.getText())
                    .map(e -> e.getModuleName().getUpperCaseIdList())
                    .orElse(upperCaseIdList);
        } else {
            return upperCaseIdList;
        }
    }
}
