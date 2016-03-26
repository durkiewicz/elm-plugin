package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.ElmLowerCasePath;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.psi.references.ElmReferenceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElmLowerCasePathImpl extends ElmPsiElement implements ElmLowerCasePath {
    public ElmLowerCasePathImpl(ASTNode node) {
        super(node);
    }

    public List<? extends ElmReference> getReferencesList() {
        return Arrays.stream(this.getChildren())
                .filter(e -> e instanceof ElmLowerCaseId)
                .map(child -> new ElmReferenceImpl(child).referenceInAncestor(this))
                .findFirst()
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }
}
