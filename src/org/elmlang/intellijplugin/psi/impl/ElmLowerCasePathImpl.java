package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.ElmLowerCasePath;
import org.elmlang.intellijplugin.psi.references.ElmReference;
import org.elmlang.intellijplugin.psi.references.ElmValueReference;

import java.util.Arrays;
import java.util.stream.Stream;

public class ElmLowerCasePathImpl extends ElmPsiElement implements ElmLowerCasePath {
    public ElmLowerCasePathImpl(ASTNode node) {
        super(node);
    }

    public Stream<ElmReference> getReferencesList() {
        return Arrays.stream(this.getChildren())
                .filter(e -> e instanceof ElmLowerCaseId)
                .map(child -> new ElmValueReference(child).referenceInAncestor(this))
                .limit(1);
    }
}
