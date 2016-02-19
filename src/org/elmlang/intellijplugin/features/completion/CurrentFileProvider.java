package org.elmlang.intellijplugin.features.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Function;
import org.elmlang.intellijplugin.psi.ElmTreeUtil;
import org.elmlang.intellijplugin.psi.ElmTypes;
import org.elmlang.intellijplugin.utils.ListUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CurrentFileProvider implements Provider {
    @Override
    public boolean addCompletions(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
        PsiElement file = parameters.getOriginalFile();
        List<PsiElement> leaves = ElmTreeUtil.getLeaves(file);
        List<String> allIdentifiers = ListUtils.unique(ListUtils.flatten(ListUtils.map(leaves, getElementTexts)));
        PsiElement position = parameters.getOriginalPosition();
        String typedText = position == null ? null : position.getText();
        int searchResult = -1;
        if (typedText != null) {
            searchResult = Collections.binarySearch(allIdentifiers, typedText, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (o1.startsWith(o2) || o2.startsWith(o1)) {
                        return 0;
                    }
                    return o1.compareTo(o2);
                }
            });
        }
        addLookupElements(resultSet, allIdentifiers);
        return searchResult >= 0;
    }

    private static void addLookupElements(@NotNull CompletionResultSet resultSet, List<String> completions) {
        for (String w : completions) {
            resultSet.addElement(LookupElementBuilder.create(w));
        }
    }

    private static final TokenSet identifiers = TokenSet.create(ElmTypes.UPPER_CASE_IDENTIFIER, ElmTypes.LOWER_CASE_IDENTIFIER);

    private static final Function<PsiElement, List<String>> getElementTexts = new Function<PsiElement, List<String>>() {
        @Override
        public List<String> fun(PsiElement element) {
            return ListUtils.map(
                    Arrays.asList(element.getNode().getChildren(identifiers)),
                    getNodeText
            );
        }
    };

    private static final Function<ASTNode, String> getNodeText = new Function<ASTNode, String>() {
        @Override
        public String fun(ASTNode node) {
            return node.getText();
        }
    };
}
