package org.elmlang.intellijplugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElmTreeUtil {
    public static boolean isAnyChildOfType(PsiElement element, IElementType type) {
        return ElmTreeUtil.isAnyMatchInChildren(element, e -> isElementOfType(e, type));
    }

    public static boolean isElementOfType(PsiElement element, IElementType type) {
        return element instanceof ASTNode && ((ASTNode) element).getElementType().equals(type);
    }

    public static boolean isAnyMatchInChildren(PsiElement element, Predicate<PsiElement> predicate) {
        return findInSiblingsStream(element.getFirstChild(), predicate)
                .isPresent();
    }

    public static Optional<PsiElement> findFollowingSibling(PsiElement element, Predicate<PsiElement> predicate) {
        return findInSiblingsStream(element.getNextSibling(), predicate);
    }

    public static String joinUsingDot(List<? extends PsiElement> elements) {
        return elements.stream()
                .map(PsiElement::getText)
                .collect(Collectors.joining("."));
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
