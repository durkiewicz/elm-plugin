package org.elmlang.intellijplugin.psi.impl;

import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFunctionDeclarationLeft;
import org.elmlang.intellijplugin.psi.ElmOperatorDeclarationLeft;
import org.elmlang.intellijplugin.psi.ElmPattern;
import org.elmlang.intellijplugin.psi.ElmValueDeclarationBase;
import org.elmlang.intellijplugin.utils.OptionalUtils;

import java.util.Optional;
import java.util.function.Function;

import static org.elmlang.intellijplugin.psi.impl.ValueDeclarationRole.*;

public class ElmValueDeclarationMixin {
    public static ValueDeclarationRole getRole(ElmValueDeclarationBase declaration) {
        return getByKind(
                declaration,
                x -> FUNCTION,
                x -> OPERATOR,
                x -> VALUE
        );
    }

    public static String getDisplayName(ElmValueDeclarationBase declaration) {
        return getByKind(
                declaration,
                ElmValueDeclarationMixin::getFunctionName,
                ElmValueDeclarationMixin::getOperatorName,
                PsiElement::getText
        );
    }

    private static String getFunctionName(ElmFunctionDeclarationLeft element) {
        return element.getLowerCaseId().getText();
    }

    private static String getOperatorName(ElmOperatorDeclarationLeft element) {
        return element.getOperatorAsFunction().getText();
    }

    private static <T> T getByKind(
            ElmValueDeclarationBase declaration,
            Function<ElmFunctionDeclarationLeft, T> functionMapper,
            Function<ElmOperatorDeclarationLeft, T> operatorMapper,
            Function<ElmPattern, T> patternMapper
    ) {
        return OptionalUtils.oneOf(
                () -> Optional.ofNullable(declaration.getFunctionDeclarationLeft())
                        .map(functionMapper),
                () -> Optional.ofNullable(declaration.getOperatorDeclarationLeft())
                        .map(operatorMapper),
                () -> Optional.ofNullable(declaration.getPattern())
                        .map(patternMapper)
        )
                .orElse(null);
    }
}