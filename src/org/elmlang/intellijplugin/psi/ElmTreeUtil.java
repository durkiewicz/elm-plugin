package org.elmlang.intellijplugin.psi;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ElmTreeUtil {
    public static List<PsiElement> getLeaves(PsiElement element) {
        PsiElement[] children = element.getChildren();
        if (children.length == 0) {
            LinkedList<PsiElement> result = new LinkedList<PsiElement>();
            result.add(element);
            return result;
        }
        ArrayList<PsiElement> result = new ArrayList<PsiElement>();
        for (PsiElement child : children) {
            result.addAll(getLeaves(child));
        }
        return result;
    }
}
