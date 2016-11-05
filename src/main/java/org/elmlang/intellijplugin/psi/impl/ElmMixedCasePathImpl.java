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
import org.elmlang.intellijplugin.psi.scope.ElmCoreLibrary;

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

        if (upperCaseIds.size() > 0 && "Native".equals(upperCaseIds.get(0).getText())) {
            return Stream.empty();
        } else if (upperCaseIds.size() == 1 && lowerCaseIds.size() == 0) {
            return getReferencesFromSingleId(upperCaseIds.get(0));
        } else if (lowerCaseIds.size() >= 1) {
            ElmReference reference = new ElmAbsoluteValueReference(lowerCaseIds.get(0));
            return this.getReferenceAndContainingModuleReference(
                    reference,
                    upperCaseIds
            );
        } else if (upperCaseIds.size() >= 2) {
            ElmUpperCaseId last = upperCaseIds.pop();
            ElmReference reference = new ElmAbsoluteTypeReference(last, upperCaseIds);
            return this.getReferenceAndContainingModuleReference(
                    reference,
                    upperCaseIds
            );
        } else {
            return Stream.empty();
        }
    }

    public List<ElmUpperCaseId> getUpperCaseIdList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ElmUpperCaseId.class);
    }

    private static Stream<ElmReference> getReferencesFromSingleId(ElmUpperCaseId element) {
        if (ElmCoreLibrary.isBuiltIn(element.getText())) {
            return Stream.empty();
        } else {
            return Stream.of(new ElmTypeReference(element));
        }
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

    private Stream<ElmReference> getReferenceAndContainingModuleReference(ElmReference reference, Stack<ElmUpperCaseId> upperCaseIds) {
        return Stream.concat(
                this.getContainingModuleReference(upperCaseIds, reference),
                Stream.of(reference.referenceInAncestor(this))
        );
    }

    private Stream<ElmReference> getContainingModuleReference(Stack<ElmUpperCaseId> upperCaseIds, ElmReference reference) {
        return this.getContainingModuleReference(getRange(upperCaseIds), upperCaseIds.size(), reference);
    }

    private Stream<ElmReference> getContainingModuleReference(TextRange textRange, int modulePartLength, ElmReference reference) {
        return Stream.of(new ElmContainingModuleReference(this, textRange, modulePartLength, reference));
    }

    private static TextRange getRange(Stack<ElmUpperCaseId> upperCaseIds) {
        ElmUpperCaseId last = upperCaseIds.peek();
        int textEnd = last.getStartOffsetInParent() + last.getTextLength();
        return new TextRange(0, textEnd);
    }
}
