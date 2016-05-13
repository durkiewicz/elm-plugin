package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.ElmMixedCasePath;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.references.*;

import java.util.*;
import java.util.stream.Stream;

public class ElmMixedCasePathImpl extends ElmPsiElement implements ElmMixedCasePath {
    public ElmMixedCasePathImpl(ASTNode node) {
        super(node);
    }

    public Stream<ElmReference> getReferencesStream() {
        Pair<Stack<ElmUpperCaseId>, Stack<ElmLowerCaseId>> pair = this.getGroupedChildren();
        Stack<ElmUpperCaseId> upperCaseIds = pair.getFirst();
        Stack<ElmLowerCaseId> lowerCaseIds = pair.getSecond();


        if (upperCaseIds.size() == 1 && lowerCaseIds.size() == 0) {
            return Stream.of(new ElmTypeReference(upperCaseIds.get(0)));
        } else if (lowerCaseIds.size() >= 1) {
            return Stream.concat(
                    getModuleReference(upperCaseIds),
                    Stream.of(new ElmAbsoluteValueReference(lowerCaseIds.get(0)).referenceInAncestor(this))
            );
        } else if (upperCaseIds.size() >= 2) {
            ElmUpperCaseId last = upperCaseIds.pop();
            TextRange moduleRange = getRange(upperCaseIds);
            return Stream.concat(
                    this.getModuleReference(moduleRange, upperCaseIds.size()),
                    Stream.of(new ElmAbsoluteTypeReference(last, moduleRange.substring(this.getText())).referenceInAncestor(this))
            );
        } else {
            return Stream.empty();
        }
    }

    public List<ElmUpperCaseId> getUpperCaseIdList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ElmUpperCaseId.class);
    }

    private Pair<Stack<ElmUpperCaseId>, Stack<ElmLowerCaseId>> getGroupedChildren() {
        Stack<ElmUpperCaseId> upperCaseIds = new Stack<>();
        Stack<ElmLowerCaseId> lowerCaseIds = new Stack<>();
        PsiElement child = this.getFirstChild();

        while (child != null) {
            if (child instanceof ElmUpperCaseId) {
                upperCaseIds.push((ElmUpperCaseId) child);
            } else if (child instanceof ElmLowerCaseId) {
                lowerCaseIds.push((ElmLowerCaseId) child);
            }
            child = child.getNextSibling();
        }

        return Pair.create(upperCaseIds, lowerCaseIds);
    }

    private Stream<ElmPartOfPathModuleReference> getModuleReference(Stack<ElmUpperCaseId> upperCaseIds) {
        return this.getModuleReference(getRange(upperCaseIds), upperCaseIds.size());
    }

    private Stream<ElmPartOfPathModuleReference> getModuleReference(TextRange textRange, int modulePartLength) {
        return Stream.of(new ElmPartOfPathModuleReference(this, textRange, modulePartLength));
    }

    private static TextRange getRange(Stack<ElmUpperCaseId> upperCaseIds) {
        ElmUpperCaseId last = upperCaseIds.peek();
        int textEnd = last.getStartOffsetInParent() + last.getTextLength();
        return new TextRange(0, textEnd);
    }
}
