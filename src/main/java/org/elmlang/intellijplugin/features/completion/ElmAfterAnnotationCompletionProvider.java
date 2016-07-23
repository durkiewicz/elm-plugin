package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmTypeAnnotation;

import java.util.Optional;

import static org.elmlang.intellijplugin.features.completion.ElmCompletionHelper.addPsiElementToResult;

class ElmAfterAnnotationCompletionProvider {
    void addCompletions(PsiElement freshLine, CompletionResultSet resultSet) {
        PsiElement prevSibling = freshLine.getPrevSibling();
        if (prevSibling instanceof ElmTypeAnnotation) {
            Optional.ofNullable(((ElmTypeAnnotation)prevSibling).getLowerCaseId())
                    .ifPresent(id -> addPsiElementToResult(id, resultSet));
        }
    }
}
