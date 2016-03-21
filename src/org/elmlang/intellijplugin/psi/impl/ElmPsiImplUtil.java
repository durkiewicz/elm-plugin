package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.psi.references.ElmReferenceImpl;

import java.util.*;
import java.util.stream.Collectors;

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
        return new ElmReferenceImpl(element);
    }

    public static PsiReference getReference(ElmUpperCaseId element) {
        return new ElmReferenceImpl(element);
    }

    public static List<ElmReference> getReferencesList(ElmExpression element) {
        List<ElmReference> result = new LinkedList<>();
        element.getListOfOperandsList().stream()
                .map(ElmPsiImplUtil::getReferencesList)
                .map(list ->
                        list.stream()
                                .map(r -> r.referenceInAncestor(element))
                                .collect(Collectors.toList())
                )
                .forEach(result::addAll);
        return result;
    }

    public static List<ElmReference> getReferencesList(ElmListOfOperands element) {
        List<ElmReference> result = new LinkedList<>();
        Arrays.stream(element.getChildren())
                .map(child -> {
                    if (child instanceof ElmWithExpression) {
                        return ElmPsiImplUtil.getReferencesList(((ElmWithExpression)child));
                    } else if (child instanceof ElmWithExpressionList) {
                        return ElmPsiImplUtil.getReferencesList(((ElmWithExpressionList)child));
                    } else if (child instanceof ElmPathBase) {
                        return ((ElmPathBase)child).getReferencesList();
                    } else {
                        return new LinkedList<ElmReference>();
                    }
                })
                .map(list ->
                        list.stream()
                                .map(r -> r.referenceInAncestor(element))
                                .collect(Collectors.toList())
                )
                .forEach(result::addAll);

        return result;
    }

    public static List<ElmReference> getReferencesList(ElmWithExpressionList element) {
        List<ElmReference> result = new LinkedList<>();
        element.getExpressionList().stream()
                .map(expr ->
                        ElmPsiImplUtil.getReferencesList(expr).stream()
                                .map(r -> r.referenceInAncestor(element))
                                .collect(Collectors.toList())
                )
                .forEach(result::addAll);
        return result;
    }

    public static List<ElmReference> getReferencesList(ElmWithExpression element) {
        return getReferencesList(element.getExpression()).stream()
                .map(r -> r.referenceInAncestor(element))
                .collect(Collectors.toList());
    }

    public static List<ElmReference> getReferencesList(ElmLowerCasePath element) {
        List<ElmReference> result = new LinkedList<>();
        for (PsiElement child : element.getChildren()) {
            if (child instanceof ElmLowerCaseId) {
                result.add(new ElmReferenceImpl(child).referenceInAncestor(element));
                break;
            }
        }
        return result;
    }

    public static List<ElmReference> getReferencesList(ElmRecord record) {
        List<ElmReference> result = new LinkedList<>();

        Optional.ofNullable(record.getLowerCaseId())
                .map(id -> new ElmReferenceImpl(id).referenceInAncestor(record))
                .ifPresent(result::add);

        record.getFieldList().stream()
                .map(ElmPsiImplUtil::getReferencesList)
                .forEach(result::addAll);

        return result;
    }
}
