package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.psi.references.ElmValueReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
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
                    } else if (child instanceof ElmLowerCasePathImpl) {
                        return ((ElmLowerCasePathImpl)child).getReferencesList();
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
                result.add(new ElmValueReference(child).referenceInAncestor(element));
                break;
            }
        }
        return result;
    }

    public static List<ElmReference> getReferencesList(ElmRecord record) {
        List<ElmReference> result = new LinkedList<>();

        Optional.ofNullable(record.getLowerCaseId())
                .map(id -> new ElmValueReference(id).referenceInAncestor(record))
                .ifPresent(result::add);

        record.getFieldList().stream()
                .map(ElmPsiImplUtil::getReferencesList)
                .forEach(result::addAll);

        return result;
    }

    @Nullable
    public static ElmUpperCasePath getModuleName(ElmModuleDeclaration module) {
        return PsiTreeUtil.findChildOfType(module, ElmUpperCasePath.class);
    }

    @Nullable
    public static ElmUpperCasePath getModuleName(ElmImportClause module) {
        return PsiTreeUtil.findChildOfType(module, ElmUpperCasePath.class);
    }

    public static boolean isExposingAll(ElmModuleDeclaration element) {
        return true;
    }

    public static boolean isExposingAll(ElmExposingClause element) {
        return true;
    }

    @NotNull
    public static List<ElmValueDeclarationBase> getValueDeclarations(ElmWithValueDeclarations element) {
        return Arrays.stream(element.getChildren())
                .filter(e -> e instanceof ElmValueDeclarationBase)
                .map(e -> (ElmValueDeclarationBase) e)
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<ElmLowerCaseId> getDeclarationsFromPattern(@Nullable ElmPattern pattern) {
        if (pattern == null) {
            return Collections.emptyList();
        }

        List<ElmLowerCaseId> result = new LinkedList<>();

        result.addAll(pattern.getLowerCaseIdList());

        addDeclarationsToResult(
                result,
                pattern.getListPatternList(),
                ElmPsiImplUtil::getDeclarationsFromParentPattern);

        addDeclarationsToResult(
                result,
                pattern.getParenthesedPatternList(),
                p -> getDeclarationsFromPattern(p.getPattern()));

        addDeclarationsToResult(
                result,
                pattern.getRecordPatternList(),
                ElmRecordPattern::getLowerCaseIdList);

        addDeclarationsToResult(
                result,
                pattern.getTuplePatternList(),
                ElmPsiImplUtil::getDeclarationsFromParentPattern);

        addDeclarationsToResult(
                result,
                pattern.getUnionPatternList(),
                ElmPsiImplUtil::getDeclarationsFromParentPattern);

        return result;
    }

    private static <T> void addDeclarationsToResult(List<ElmLowerCaseId> result, List<T> source, Function<T, List<ElmLowerCaseId>> f) {
        source.stream()
                .map(f)
                .forEach(result::addAll);
    }

    private static List<ElmLowerCaseId> getDeclarationsFromParentPattern(ElmWithPatternList parentPattern) {
        List<ElmLowerCaseId> result = new LinkedList<>();
        parentPattern.getPatternList().stream()
                .map(ElmPsiImplUtil::getDeclarationsFromPattern)
                .forEach(result::addAll);
        return result;
    }

    public static List<ElmLowerCaseId> getDefinedValues(ElmValueDeclarationBase element) {
        List<ElmLowerCaseId> result = new LinkedList<>();
        Arrays.stream(element.getChildren())
                .map(child -> {
                    if (child instanceof ElmPattern) {
                        return getDeclarationsFromPattern((ElmPattern) child);
                    } else if (child instanceof ElmWithSingleId) {
                        return Collections.singletonList(((ElmWithSingleId) child).getLowerCaseId());
                    } else {
                        return Collections.<ElmLowerCaseId>emptyList();
                    }
                })
                .forEach(result::addAll);
        return result;
    }
}
