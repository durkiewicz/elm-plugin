package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.elmlang.intellijplugin.psi.ElmElementFactory;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.ElmTypes;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.references.ElmReference;

public class ElmPsiImplUtil {
    public static String getName(ElmUpperCaseId element) {
        return element.getText();
    }

    public static PsiElement setName(ElmUpperCaseId element, String newName) {
        ASTNode node = element.getNode().findChildByType(ElmTypes.UPPER_CASE_IDENTIFIER);
        if (node != null) {
            ElmUpperCaseId id = ElmElementFactory.createUpperCaseId(element.getProject(), newName);
            ASTNode newNode = id.getFirstChild().getNode();
            element.getNode().replaceChild(node, newNode);
        }
        return element;
    }

    public static String getName(ElmLowerCaseId element) {
        return element.getText();
    }

    public static PsiElement setName(ElmLowerCaseId element, String newName) {
        ASTNode node = element.getNode().findChildByType(ElmTypes.UPPER_CASE_IDENTIFIER);
        if (node != null) {
            ElmLowerCaseId id = ElmElementFactory.createLowerCaseId(element.getProject(), newName);
            ASTNode newNode = id.getFirstChild().getNode();
            element.getNode().replaceChild(node, newNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(ElmLowerCaseId element) {
        ASTNode node = element.getNode();
        if (node != null) {
            return node.getPsi();
        } else {
            return null;
        }
    }

    public static PsiElement getNameIdentifier(ElmUpperCaseId element) {
        ASTNode node = element.getNode();
        if (node != null) {
            return node.getPsi();
        } else {
            return null;
        }
    }

    public static PsiReference getReference(ElmLowerCaseId element) {
        return new ElmReference(element, element.getTextRange());
    }
}
