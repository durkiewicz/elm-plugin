package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.elmlang.intellijplugin.psi.impl.ElmPsiElement;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ElmTreeUtil {
    public static List<PsiElement> getLeaves(PsiElement element) {
        PsiElement[] children = element.getChildren();
        LinkedList<PsiElement> result = new LinkedList<PsiElement>();
        if (children.length == 0) {
            result.add(element);
        } else {
            for (PsiElement child : children) {
                result.addAll(getLeaves(child));
            }
        }
        return result;
    }

    public static List<ElmLowerCaseId> getClosureDeclarations(PsiElement elem) {
        List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();
        PsiElement ancestor = elem.getParent();

//        ElmCaseOfBranch +
//        ElmPattern +
//        ElmInnerValueDeclaration +
//        ElmInnerTypeAnnotation
//        ElmLetIn +
//        ElmValueDeclaration +
//        ElmTypeAliasDeclaration
//        ElmTypeDeclaration
//        ElmTypeAnnotation

        while (ancestor != null) {
            if (ancestor instanceof ElmPattern) {
                break;
            } else if (ancestor instanceof ElmCaseOfBranch) {
                ElmCaseOfBranch caseOfBranch = (ElmCaseOfBranch) ancestor;
                result.addAll(getDeclarationsFromPattern(caseOfBranch.getPattern()));
            } else if (ancestor instanceof ElmInnerValueDeclaration) {
                result.addAll(getDeclarationsFromValue((ElmInnerValueDeclaration) ancestor, false));
            } else if (ancestor instanceof ElmLetIn) {
                result.addAll(getDeclarationsFromLetIn((ElmLetIn) ancestor));
            } else if (ancestor instanceof ElmValueDeclaration) {
                result.addAll(getDeclarationsFromTopValue((ElmValueDeclaration) ancestor));
            } else if (ancestor instanceof ElmAnonymousFunction) {
                result.addAll(getDeclarationsFromAnonymousFunction((ElmAnonymousFunction) ancestor));
            }

            ancestor = ancestor.getParent();
        }
        return result;
    }

    private static List<ElmLowerCaseId> getDeclarationsFromAnonymousFunction(ElmAnonymousFunction element) {
        List<ElmLowerCaseId> result = new LinkedList<>();
        element.getPatternList().stream()
                .map(ElmTreeUtil::getDeclarationsFromPattern)
                .forEach(result::addAll);
        return result;
    }

    private static List<ElmLowerCaseId> getDeclarationsFromTopValue(ElmValueDeclaration valueDeclaration) {
        List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();
        ElmPortDeclarationLeft portLeft;
        result.addAll(getDeclarationsFromValue(valueDeclaration, false));
        if ((portLeft = valueDeclaration.getPortDeclarationLeft()) != null) {
            result.add(portLeft.getLowerCaseId());
        }
        return result;
    }

    private static List<ElmLowerCaseId> getDeclarationsFromLetIn(ElmLetIn letIn) {
        List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();

        addDeclarationsToResult(
                result,
                letIn.getInnerValuesList(),
                v -> getDeclarationsFromValue(v, true));

        return result;
    }

    private static List<ElmLowerCaseId> getDeclarationsFromValue(ElmValueDeclarationBase innerElmValueDeclarationBase, boolean skipInternals) {
        List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();
        ElmFunctionDeclarationLeft functionLeft;
        ElmOperatorDeclarationLeft operatorLeft;
        List<ElmPattern> patternList = Collections.emptyList();
        if (innerElmValueDeclarationBase.getPattern() != null) {
            result.addAll(getDeclarationsFromPattern(innerElmValueDeclarationBase.getPattern()));
        } else if ((functionLeft = innerElmValueDeclarationBase.getFunctionDeclarationLeft()) != null) {
            result.add(functionLeft.getLowerCaseId());
            patternList = functionLeft.getPatternList();
        } else if ((operatorLeft = innerElmValueDeclarationBase.getOperatorDeclarationLeft()) != null) {
            patternList = operatorLeft.getPatternList();
        }
        if (!skipInternals) {
            patternList.stream()
                    .map(ElmTreeUtil::getDeclarationsFromPattern)
                    .forEach(result::addAll);
        }
        return result;
    }

    private static List<ElmLowerCaseId> getDeclarationsFromPattern(ElmPattern pattern) {
        if (pattern == null) {
            return Collections.emptyList();
        }

        List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();

        result.addAll(pattern.getLowerCaseIdList());

        addDeclarationsToResult(
                result,
                pattern.getListPatternList(),
                ElmTreeUtil::getDeclarationsFromParentPattern);

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
                ElmTreeUtil::getDeclarationsFromParentPattern);

        addDeclarationsToResult(
                result,
                pattern.getUnionPatternList(),
                ElmTreeUtil::getDeclarationsFromParentPattern);

        return result;
    }

    private static <T> void addDeclarationsToResult(List<ElmLowerCaseId> result, List<T> source, Function<T, List<ElmLowerCaseId>> f) {
        source.stream()
                .map(f)
                .forEach(result::addAll);
    }

    private static <T extends ElmWithPatternList> List<ElmLowerCaseId> getDeclarationsFromParentPattern(ElmWithPatternList parentPattern) {
        List<ElmLowerCaseId> result = new LinkedList<>();
        parentPattern.getPatternList().stream()
                .map(ElmTreeUtil::getDeclarationsFromPattern)
                .forEach(result::addAll);
        return result;
    }
}
