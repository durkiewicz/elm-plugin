package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import org.elmlang.intellijplugin.utils.ListUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
                result.addAll(getDeclarationsFromPattern.fun(caseOfBranch.getPattern()));
            } else if (ancestor instanceof ElmInnerValueDeclaration) {
                result.addAll(getDeclarationsFromValue((ElmInnerValueDeclaration) ancestor, false));
            } else if (ancestor instanceof ElmLetIn) {
                result.addAll(getDeclarationsFromLetIn((ElmLetIn)ancestor));
            } else if (ancestor instanceof ElmValueDeclaration) {
                result.addAll(getDeclarationsFromTopValue((ElmValueDeclaration)ancestor));
            }
            ancestor = ancestor.getParent();
        }
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
                getExposedDeclarationsFromInnerValue);

        return result;
    }

    private static List<ElmLowerCaseId> getDeclarationsFromValue(ElmValueDeclarationBase innerElmValueDeclarationBase, boolean skipInternals) {
        List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();
        ElmFunctionDeclarationLeft functionLeft;
        ElmOperatorDeclarationLeft operatorLeft;
        List<ElmPattern> patternList = Collections.emptyList();
        if (innerElmValueDeclarationBase.getPattern() != null) {
            result.addAll(getDeclarationsFromPattern.fun(innerElmValueDeclarationBase.getPattern()));
        } else if ((functionLeft = innerElmValueDeclarationBase.getFunctionDeclarationLeft()) != null) {
            result.add(functionLeft.getLowerCaseId());
            patternList = functionLeft.getPatternList();
        } else if ((operatorLeft = innerElmValueDeclarationBase.getOperatorDeclarationLeft()) != null) {
            patternList = operatorLeft.getPatternList();
        }
        if (!skipInternals) {
            result.addAll(
                    ListUtils.flatten(
                            ListUtils.map(
                                    patternList,
                                    getDeclarationsFromPattern
                            )
                    )
            );
        }
        return result;
    }

    private static final Function<ElmPattern, List<ElmLowerCaseId>> getDeclarationsFromPattern = new Function<ElmPattern, List<ElmLowerCaseId>>() {
        @Override
        public List<ElmLowerCaseId> fun(ElmPattern pattern) {
            if (pattern == null) {
                return Collections.emptyList();
            }

            List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();

            result.addAll(pattern.getLowerCaseIdList());

            addDeclarationsToResult(
                    result,
                    pattern.getListPatternList(),
                    getDeclarationsFromList);

            addDeclarationsToResult(
                    result,
                    pattern.getParenthesedPatternList(),
                    getDeclarationsFromParenthesedPattern);

            addDeclarationsToResult(
                    result,
                    pattern.getRecordPatternList(),
                    getDeclarationsFromRecord);

            addDeclarationsToResult(
                    result,
                    pattern.getTuplePatternList(),
                    getDeclarationsFromTuple);

            addDeclarationsToResult(
                    result,
                    pattern.getUnionPatternList(),
                    getDeclarationsFromUnion);

            return result;
        }
    };

    private static <T> void addDeclarationsToResult(List<ElmLowerCaseId> result, List<T> source, Function<T, List<ElmLowerCaseId>> f) {
        List<ElmLowerCaseId> newElements =
                ListUtils.flatten(
                        ListUtils.map(source, f)
                );
        result.addAll(newElements);
    }

    private static final Function<ElmParenthesedPattern, List<ElmLowerCaseId>> getDeclarationsFromParenthesedPattern =
            new Function<ElmParenthesedPattern, List<ElmLowerCaseId>>() {
                @Override
                public List<ElmLowerCaseId> fun(ElmParenthesedPattern p) {
                    return getDeclarationsFromPattern.fun(p.getPattern());
                }
            };

    private static final Function<ElmRecordPattern, List<ElmLowerCaseId>> getDeclarationsFromRecord =
            new Function<ElmRecordPattern, List<ElmLowerCaseId>>() {
                @Override
                public List<ElmLowerCaseId> fun(ElmRecordPattern recordPattern) {
                    return recordPattern.getLowerCaseIdList();
                }
            };

    private static <T extends ElmWithPatternList> Function<T, List<ElmLowerCaseId>> getDeclarationsFromParentPattern() {
        return new Function<T, List<ElmLowerCaseId>>() {
            @Override
            public List<ElmLowerCaseId> fun(ElmWithPatternList parentPattern) {
                List<ElmLowerCaseId> result = new LinkedList<ElmLowerCaseId>();
                for (ElmPattern p : parentPattern.getPatternList()) {
                    result.addAll(getDeclarationsFromPattern.fun(p));
                }
                return result;
            }
        };
    }

    private static final Function<ElmTuplePattern, List<ElmLowerCaseId>> getDeclarationsFromTuple =
            getDeclarationsFromParentPattern();

    private static final Function<ElmListPattern, List<ElmLowerCaseId>> getDeclarationsFromList =
            getDeclarationsFromParentPattern();

    private static final Function<ElmUnionPattern, List<ElmLowerCaseId>> getDeclarationsFromUnion =
            getDeclarationsFromParentPattern();

    private static final Function<ElmInnerValueDeclaration, List<ElmLowerCaseId>> getExposedDeclarationsFromInnerValue =
            new Function<ElmInnerValueDeclaration, List<ElmLowerCaseId>>() {
                @Override
                public List<ElmLowerCaseId> fun(ElmInnerValueDeclaration innerValueDeclaration) {
                    return getDeclarationsFromValue(innerValueDeclaration, true);
                }
            };
}
