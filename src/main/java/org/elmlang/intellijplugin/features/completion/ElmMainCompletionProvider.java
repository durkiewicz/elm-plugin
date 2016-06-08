package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.ASTNode;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.elmlang.intellijplugin.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class ElmMainCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final ElmValueCompletionProvider valueProvider;
    private final ElmKeywordsCompletionsProvider keywordsProvider;
    private final ElmTypeCompletionProvider typeProvider;
    private final ElmModuleCompletionProvider moduleProvider;
    private final ElmAbsoluteValueCompletionProvider absoluteValueProvider;

    ElmMainCompletionProvider(ElmValueCompletionProvider valueProvider, ElmKeywordsCompletionsProvider keywordsProvider, ElmTypeCompletionProvider typeProvider, ElmModuleCompletionProvider moduleProvider, ElmAbsoluteValueCompletionProvider absoluteValueProvider) {
        this.valueProvider = valueProvider;
        this.keywordsProvider = keywordsProvider;
        this.typeProvider = typeProvider;
        this.moduleProvider = moduleProvider;
        this.absoluteValueProvider = absoluteValueProvider;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        PsiElement position = parameters.getPosition();
        PsiElement parent = position.getParent();
        PsiElement grandParent = Optional.ofNullable(parent)
                .map(PsiElement::getParent)
                .orElse(null);
        if (parent instanceof ElmFile || parent instanceof GeneratedParserUtilBase.DummyBlock) {
            addCompletionsInInvalidExpression(position, resultSet);
        } else if (grandParent instanceof ElmLowerCasePath
                && parent.getStartOffsetInParent() == 0) {
            this.valueProvider.addCompletions((ElmLowerCaseId) parent, resultSet);
            this.keywordsProvider.addCompletions(resultSet);
        } else if (grandParent instanceof ElmMixedCasePath
                || grandParent instanceof ElmUpperCasePath) {
            addTypeOrModuleCompletions(parent, resultSet);
        }
    }

    private void addTypeOrModuleCompletions(PsiElement element, CompletionResultSet resultSet) {
        ElmFile file = (ElmFile) element.getContainingFile();
        if (element.getStartOffsetInParent() == 0) {
            this.typeProvider.addCompletions(file, resultSet);
            this.moduleProvider.addCompletions(file, resultSet);
        } else {
            addModuleCompletions(element, resultSet);
        }
    }

    private void addModuleCompletions(PsiElement element, CompletionResultSet resultSet) {
        PsiElement prevSibling = element.getPrevSibling();
        PsiElement prevPrevSibling = Optional.ofNullable(prevSibling)
                .flatMap(e -> Optional.ofNullable(e.getPrevSibling()))
                .orElse(null);
        if (!(prevSibling instanceof ASTNode)
                || !((ASTNode) prevSibling).getElementType().equals(ElmTypes.DOT)) {
            return;
        }
        if (prevPrevSibling instanceof ElmUpperCaseId) {
            addCompletionsAfterDot(prevSibling, resultSet);
        }
    }

    private void addCompletionsInInvalidExpression(@NotNull PsiElement position, @NotNull CompletionResultSet resultSet) {
        PsiElement prevSibling = position.getPrevSibling();
        if (prevSibling instanceof PsiWhiteSpace) {
            addCompletionsAfterWhiteSpace(position, resultSet);
        } else if (prevSibling instanceof ASTNode) {
            addCompletionsAfterASTNode((ASTNode) prevSibling, resultSet);
        }
    }

    private void addCompletionsAfterASTNode(ASTNode node, CompletionResultSet resultSet) {
        IElementType elementType = node.getElementType();
        if (elementType.equals(ElmTypes.DOT)) {
            addCompletionsAfterDot((PsiElement) node, resultSet);
        } else if (elementType.equals(ElmTypes.FRESH_LINE)) {
            this.keywordsProvider.addCompletions(resultSet);
        }
    }

    private void addCompletionsAfterWhiteSpace(PsiElement position, CompletionResultSet resultSet) {
        char firstChar = position.getText().charAt(0);
        ElmFile file = (ElmFile) position.getContainingFile();
        if (Character.isLowerCase(firstChar)) {
            this.valueProvider.addCompletions(file, resultSet);
            this.keywordsProvider.addCompletions(resultSet);
        } else if (Character.isUpperCase(firstChar)) {
            this.typeProvider.addCompletions(file, resultSet);
            this.moduleProvider.addCompletions(file, resultSet);
        }
    }

    private void addCompletionsAfterDot(PsiElement dot, @NotNull CompletionResultSet resultSet) {
        List<PsiElement> upperCaseIds = new LinkedList<>();
        PsiElement elem = dot.getPrevSibling();
        while (true) {
            if (elem == null) {
                break;
            } else if (elem instanceof ElmUpperCaseId) {
                upperCaseIds.add(0, elem);
            } else if (elem instanceof ASTNode) {
                IElementType type = ((ASTNode) elem).getElementType();
                if (type.equals(ElmTypes.UPPER_CASE_IDENTIFIER)) {
                    upperCaseIds.add(0, elem);
                } else if (!type.equals(ElmTypes.DOT)) {
                    break;
                }
            }
            elem = elem.getPrevSibling();
        }
        if (upperCaseIds.size() > 0) {
            String modulePart = ElmTreeUtil.joinUsingDot(upperCaseIds);
            this.moduleProvider.addCompletions(dot.getProject(), modulePart, resultSet);
            this.absoluteValueProvider.addCompletions((ElmFile) dot.getContainingFile(), modulePart, resultSet);
        }
    }
}
