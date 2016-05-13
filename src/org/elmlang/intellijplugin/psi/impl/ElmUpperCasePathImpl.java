package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.ElmImportClause;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.elmlang.intellijplugin.psi.ElmUpperCasePath;
import org.elmlang.intellijplugin.psi.references.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElmUpperCasePathImpl extends ElmPsiElement implements ElmUpperCasePath {
    public ElmUpperCasePathImpl(ASTNode node) {
        super(node);
    }

    public List<ElmUpperCaseId> getUpperCaseIdList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ElmUpperCaseId.class);
    }

    public Stream<ElmReference> getReferencesStream() {
        if (this.getParent() instanceof ElmImportClause) {
            return Stream.of(new ElmFullPathModuleReference(this));
        }

        List<ElmUpperCaseId> children = Arrays.stream(this.getChildren())
                .filter(e -> e instanceof ElmUpperCaseId)
                .map(e -> (ElmUpperCaseId) e)
                .collect(Collectors.toList());
        int size = children.size();
        if (size == 1) {
            return Stream.of(new ElmTypeReference(children.get(0)));
        } else if (size >= 2) {
            return getReferencesFromNonSinglePath(size, children.get(size - 1));
        }

        return Stream.empty();
    }

    private Stream<ElmReference> getReferencesFromNonSinglePath(int pathLength, ElmUpperCaseId lastChild) {
        int moduleTextLength = this.getTextLength() - lastChild.getTextLength() - 1;
        if (moduleTextLength < 0) {
            moduleTextLength = 0;
        }

        return Stream.concat(
                Stream.of(new ElmPartOfPathModuleReference(this, new TextRange(0, moduleTextLength), pathLength - 1)),
                Stream.of(new ElmAbsoluteTypeReference(lastChild, this.getText().substring(0, moduleTextLength)))
        );
    }
}
