package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;

import java.util.LinkedList;
import java.util.List;

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
}
