package org.elmlang.intellijplugin.psi.references.utils;

import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmTreeUtil;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;

import java.util.List;

public class AbsoluteReferencesHelper {
    public static String getModuleName(List<ElmUpperCaseId> upperCaseIdList) {
        return ElmTreeUtil.joinUsingDot(getModuleNameAsList(upperCaseIdList));
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
