package org.elmlang.intellijplugin.features;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.elmlang.intellijplugin.psi.ElmTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElmPairedBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[] {
            new BracePair(ElmTypes.LEFT_BRACE, ElmTypes.RIGHT_BRACE, true),
            new BracePair(ElmTypes.LEFT_SQUARE_BRACKET, ElmTypes.RIGHT_SQUARE_BRACKET, true),
            new BracePair(ElmTypes.LEFT_PARENTHESIS, ElmTypes.RIGHT_PARENTHESIS, false)
    };

    @Override
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, @Nullable IElementType iElementType1) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile psiFile, int i) {
        return i;
    }
}
