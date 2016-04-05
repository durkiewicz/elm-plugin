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
        return getChildrenStream(element)
                .filter(elem -> elem.map(predicate::test).orElse(true))
                .findFirst()
                .orElse(Optional.empty())
                .isPresent();
    }

    private static Stream<Optional<PsiElement>> getChildrenStream(PsiElement element) {
        return Stream.iterate(
                Optional.ofNullable(element.getFirstChild()),
                prev -> prev.map(e -> Optional.ofNullable(e.getNextSibling())).orElse(Optional.empty())
                );
    }
}
