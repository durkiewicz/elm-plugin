package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.Consumer;
import com.intellij.util.ProcessingContext;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.psi.scope.ElmScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

class ElmCompletionProvider extends CompletionProvider<CompletionParameters> {
    private static final String[] KEYWORDS = new String[] {
            "module",
            "where",
            "import",
            "as",
            "exposing",
            "type",
            "alias",
            "case",
            "of",
            "let",
            "in",
            "infix",
            "infixl",
            "infixr",
            "if",
            "then",
            "else"
    };

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        PsiElement position = parameters.getOriginalPosition();
        if (position == null) {
            return;
        }
        PsiElement parent = position.getParent();
        PsiElement grandParent = Optional.ofNullable(parent)
                .map(PsiElement::getParent)
                .orElse(null);
        if (parent instanceof ElmFile) {
            addCompletionsInInvalidExpression(position, resultSet);
        } else if (grandParent instanceof ElmLowerCasePath
                && parent.getStartOffsetInParent() == 0) {
            addValueCompletions(
                    ElmScope.scopeFor((ElmLowerCaseId) parent),
                    resultSet
            );
        } else if ((grandParent instanceof ElmMixedCasePath
                || grandParent instanceof ElmUpperCasePath)
                && parent.getStartOffsetInParent() == 0
                ) {
            addTypesCompletions((ElmFile) parent.getContainingFile(), resultSet);
        }
    }

    private void addCompletionsInInvalidExpression(@NotNull PsiElement position, @NotNull CompletionResultSet resultSet) {
        if (position.getPrevSibling() instanceof PsiWhiteSpace) {
            addCompletionsAfterWhiteSpace(position, resultSet);
        }
    }

    private static void addCompletionsAfterWhiteSpace(PsiElement position, CompletionResultSet resultSet) {
        char firstChar = position.getText().charAt(0);
        ElmFile file = (ElmFile) position.getContainingFile();
        if (Character.isLowerCase(firstChar)) {
            addValueCompletions(ElmScope.scopeFor(file), resultSet);
        } else if (Character.isUpperCase(firstChar)) {
            addTypesCompletions(file, resultSet);
        }
    }

    private static void addValueCompletions(Stream<Optional<ElmLowerCaseId>> stream, CompletionResultSet resultSet) {
        forEachUntilPresent(
                stream,
                id -> addPsiElementToResult(id, resultSet)
        );
        addKeyWords(resultSet);
    }

    private static void addTypesCompletions(ElmFile file, CompletionResultSet resultSet) {
        forEachUntilPresent(
                ElmScope.typesFor(file),
                id -> addPsiElementToResult(id, resultSet)
        );
    }

    private static <T> void forEachUntilPresent(Stream<Optional<T>> stream, Consumer<T> consumer) {
        stream.peek(e -> e.ifPresent(consumer::consume))
                .filter(e -> !e.isPresent())
                .findFirst();
    }

    private static void addKeyWords(CompletionResultSet resultSet) {
        Arrays.stream(KEYWORDS)
                .forEach(s -> addStringToResult(s, resultSet));
    }

    private static void addPsiElementToResult(PsiElement element, CompletionResultSet resultSet) {
        addStringToResult(element.getText(), resultSet);
    }

    private static void addStringToResult(String string, CompletionResultSet resultSet) {
        resultSet.addElement(LookupElementBuilder.create(string));
    }
}
