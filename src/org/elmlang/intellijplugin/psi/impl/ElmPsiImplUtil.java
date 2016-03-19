package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.utils.ListUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

    public static PsiReference[] getReferences(ElmLowerCaseId element) {
        return new PsiReference[]{getReference(element)};
    }

    public static PsiReference findReferenceAt(ElmLowerCaseId element, int offset) {
        return getReference(element);
    }

    public static PsiReference getReference(ElmUpperCaseId element) {
        return new ElmReference(element, element.getTextRange());
    }

    public static PsiReference[] getReferences(ElmUpperCaseId element) {
        return new PsiReference[]{getReference(element)};
    }

    public static PsiReference findReferenceAt(ElmUpperCaseId element, int offset) {
        return getReference(element);
    }

    public static List<PsiReference> getReferencesList(ElmExpression element) {
        return ListUtils.flatten(
                ListUtils.map(
                        element.getListOfOperandsList(),
                        ElmPsiImplUtil::getReferencesList
                )
        );
    }

    public static List<PsiReference> getReferencesList(ElmListOfOperands element) {
        return ListUtils.flatten(
                ListUtils.map(
                        element.getAnonymousFunctionList(),
                        ElmPsiImplUtil::getReferencesList
                ),
                ListUtils.map(
                        element.getIfElseList(),
                        ElmPsiImplUtil::getReferencesList
                ),
                ListUtils.map(
                        element.getListList(),
                        ElmPsiImplUtil::getReferencesList
                ),
                ListUtils.map(
                        element.getListRangeList(),
                        ElmPsiImplUtil::getReferencesList
                ),
                ListUtils.map(
                        element.getNonEmptyTupleList(),
                        ElmPsiImplUtil::getReferencesList
                ),
                ListUtils.map(
                        element.getParenthesedExpressionList(),
                        ElmPsiImplUtil::getReferencesList
                ),
                ListUtils.map(
                        PsiTreeUtil.getChildrenOfTypeAsList(element, ElmLowerCasePath.class),
                        ElmPsiImplUtil::getReferencesList
                )
        );
    }

    public static List<PsiReference> getReferencesList(ElmWithExpressionList element) {
        return ListUtils.flatten(
                ListUtils.map(
                        element.getExpressionList(),
                        ElmPsiImplUtil::getReferencesList
                )
        );
    }

    public static List<PsiReference> getReferencesList(ElmWithExpression element) {
        return getReferencesList(element.getExpression());
    }

    public static List<PsiReference> getReferencesList(ElmLowerCasePath element) {
        List<PsiReference> result = new LinkedList<>();
        for (PsiElement child : element.getChildren()) {
            if (child instanceof ElmLowerCaseId) {
                result.add(new ElmReference(child, child.getTextRange()));
                break;
            }
        }
        return result;
    }

    public static List<PsiReference> getReferencesList(ElmRecord record) {
        List<PsiReference> result = new LinkedList<>();

        Optional.ofNullable(record.getLowerCaseId())
                .map(e -> new ElmReference(e, e.getTextRange()))
                .ifPresent(result::add);

        record.getFieldList().stream()
                .map(ElmPsiImplUtil::getReferencesList)
                .forEach(result::addAll);

        return result;
    }
}
