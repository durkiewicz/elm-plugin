package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.elmlang.intellijplugin.psi.ElmTypes;
import org.elmlang.intellijplugin.psi.impl.*;

public class ElmManualPsiElementFactory {

    public static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == ElmTypes.CASE_OF) {
            return new ElmCaseOfImpl(node);
        }
        if (type == ElmTypes.LET_IN) {
            return new ElmLetInImpl(node);
        }
        if (type == ElmTypes.UPPER_CASE_PATH) {
            return new ElmUpperCasePathImpl(node);
        }
        if (type == ElmTypes.LOWER_CASE_PATH) {
            return new ElmLowerCasePathImpl(node);
        }
        if (type == ElmTypes.MIXED_CASE_PATH) {
            return new ElmMixedCasePathImpl(node);
        }
        return null;
    }
}