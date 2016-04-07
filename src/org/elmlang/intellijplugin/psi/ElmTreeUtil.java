package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ElmTreeUtil {
    public static List<PsiElement> getLeaves(PsiElement element) {
        PsiElement[] children = element.getChildren();
        LinkedList<PsiElement> result = new LinkedList<>();
        if (children.length == 0) {
            result.add(element);
        } else {
            for (PsiElement child : children) {
                result.addAll(getLeaves(child));
            }
        }
        return result;
    }

    public static boolean isAnyMatchInChildren(PsiElement element, Predicate<PsiElement> predicate) {
        return findInSiblingsStream(element.getFirstChild(), predicate)
                .isPresent();
    }

    public static Optional<PsiElement> findFollowingSibling(PsiElement element, Predicate<PsiElement> predicate) {
        return findInSiblingsStream(element.getNextSibling(), predicate);
    }

    private static Optional<PsiElement> findInSiblingsStream(PsiElement element, Predicate<PsiElement> predicate) {
        return getSiblingsStream(element)
                .filter(elem -> elem.map(predicate::test).orElse(true))
                .findFirst()
                .orElse(Optional.empty());
    }

    private static Stream<Optional<PsiElement>> getSiblingsStream(PsiElement element) {
        return Stream.iterate(
                Optional.ofNullable(element),
                prev -> prev.map(e -> Optional.ofNullable(e.getNextSibling())).orElse(Optional.empty())
        );
    }
}
