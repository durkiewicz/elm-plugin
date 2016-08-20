package org.elmlang.intellijplugin.features;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.elmlang.intellijplugin.ElmLexerAdapter;
import org.elmlang.intellijplugin.ElmParserDefinition;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;
import org.elmlang.intellijplugin.psi.ElmTypes;
import org.elmlang.intellijplugin.psi.ElmUpperCaseId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElmFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new ElmLexerAdapter(),
                TokenSet.create(
                        ElmTypes.UPPER_CASE_IDENTIFIER,
                        ElmTypes.LOWER_CASE_IDENTIFIER
                ),
                ElmParserDefinition.COMMENTS,
                TokenSet.create(
                        ElmTypes.STRING_LITERAL,
                        ElmTypes.CHAR_LITERAL,
                        ElmTypes.NUMBER_LITERAL
                )
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof ElmLowerCaseId || psiElement instanceof ElmUpperCaseId;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement psiElement) {
        return "";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement psiElement) {
        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement psiElement, boolean b) {
        return "";
    }
}
