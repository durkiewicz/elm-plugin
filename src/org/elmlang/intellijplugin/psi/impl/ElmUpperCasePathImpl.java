package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.ElmUpperCasePath;

import java.util.List;

public class ElmUpperCasePathImpl extends ElmPsiElement implements ElmUpperCasePath {
    public ElmUpperCasePathImpl(ASTNode node) {
        super(node);
    }

    public List<ElmUpperCaseId> getUpperCaseIdList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ElmUpperCaseId.class);
    }
}
