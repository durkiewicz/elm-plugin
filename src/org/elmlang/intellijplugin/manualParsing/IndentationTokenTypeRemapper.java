package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.ITokenTypeRemapper;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.*;

import org.elmlang.intellijplugin.psi.ElmTypes;
import org.jetbrains.annotations.Nullable;


public class IndentationTokenTypeRemapper implements ITokenTypeRemapper {
    private static final Map<Long, IndentationTokenTypeRemapper> instances =
            Collections.synchronizedMap(new HashMap<Long, IndentationTokenTypeRemapper>());

    private Stack<Indentation> indentations;

    private IndentationTokenTypeRemapper() {
        this.reset();
    }

    public void pushIndentation(int indentation, IndentationType type) {
        if (indentation > 0) {
            Indentation value = new Indentation(indentation, this.indentations.size(), type);
            this.indentations.push(value);
        }
    }

    public void popIndentation(int indentationValue, EnumSet<IndentationType> types) {
        if (indentationValue == 0) {
            return;
        }
        Indentation indentation = this.getIndentation(indentationValue);
        if (indentation != null) {
            this.removeIndentations(indentation.index, types);
        }
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
                    this.removeCaseIndentationsAbove(indentation);
                    return ElmTypes.CASE_OF_SEPARATION;
                }
            }
        } else if (ElmTypes.FRESH_LINE.equals(type) || end == text.length()) {
            this.reset();
        }
        return type;
    }

    public static IndentationTokenTypeRemapper getInstance() {
        long key = Thread.currentThread().getId();
        if (!instances.containsKey(key)) {
            instances.put(key, new IndentationTokenTypeRemapper());
        }
        return instances.get(key);
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

    private void removeCaseIndentationsAbove(Indentation indentation) {
        this.removeIndentations(indentation.index + 1, EnumSet.of(IndentationType.CASE_OF));
    }

    private void removeIndentations(int minIndex, EnumSet<IndentationType> types) {
        for (int i = this.indentations.size() - 1; i >= minIndex; i--) {
            Indentation toRemove = this.indentations.elementAt(i);
            if (types.contains(toRemove.type)) {
                this.indentations.removeElementAt(i);
            }
        }
    }

    public enum IndentationType {
        CASE_OF,
        LET_IN
    }

    private class Indentation {
        private final int value;
        private final int index;
        private final IndentationType type;

        private Indentation(int value, int index, IndentationType type) {
            this.value = value;
            this.index = index;
            this.type = type;
        }

        public String toString() {
            return String.format("(%d, %d, %s)", this.index, this.value, this.type);
        }
    }
}
