package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.ITokenTypeRemapper;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.elmlang.intellijplugin.psi.ElmTypes;
import org.jetbrains.annotations.Nullable;

public class IndentationTokenTypeRemapper implements ITokenTypeRemapper {
    private static final Map<Long, IndentationTokenTypeRemapper> instances =
            new HashMap<Long, IndentationTokenTypeRemapper>();

    private Stack<Indentation> indentations;

    private IndentationTokenTypeRemapper() {
        this.reset();
    }

    public int pushIndentation(PsiBuilder builder) {
        int indentation = IndentationHelper.getIndentationOfPreviousToken(builder);
        if (indentation > 0) {
            this.indentations.push(new Indentation(indentation, this.indentations.size()));
        }
        return indentation;
    }

    public void reset() {
        if (this.indentations == null || !this.indentations.empty()) {
            this.indentations = new Stack<Indentation>();
        }
    }

    @Override
    public IElementType filter(IElementType type, int start, int end, CharSequence text) {
        if (!this.indentations.empty() && TokenType.WHITE_SPACE.equals(type)) {
            int i = IndentationHelper.getIndentation(text, start, end);
            if (i > 0) {
                Indentation indentation = this.getIndentation(i);
                if (indentation != null) {
                    if (!this.isLast(indentation)) {
                        this.removeAbove(indentation);
                    }
                    return ElmTypes.CASE_OF_SEPARATION;
                }
            }
        } else if (ElmTypes.FRESH_LINE.equals(type)) {
            this.reset();
        }
        return type;
    }

    public static IndentationTokenTypeRemapper getInstance() {
        long threadId = Thread.currentThread().getId();
        if (!instances.containsKey(threadId)) {
            instances.put(threadId, new IndentationTokenTypeRemapper());
        }
        return instances.get(threadId);
    }

    @Nullable
    private Indentation getIndentation(int indentation) {
        for (int i = this.indentations.size() - 1; i >= 0; i--) {
            Indentation ind = this.indentations.elementAt(i);
            if (ind.value == indentation) {
                return ind;
            }
        }
        return null;
    }

    private boolean isLast(Indentation indentation) {
        return indentation.index == this.indentations.size() - 1;
    }

    private void removeAbove(Indentation indentation) {
        Indentation removed;
        while (true) {
            removed = this.indentations.pop();
            if (removed.index == indentation.index + 1) {
                break;
            }
        }
    }

    private class Indentation {
        private final int value;
        private final int index;

        private Indentation(int value, int index) {
            this.value = value;
            this.index = index;
        }

        public String toString() {
            return String.format("(%d, %d)", this.index, this.value);
        }
    }
}
