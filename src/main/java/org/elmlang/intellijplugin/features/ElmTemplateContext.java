package org.elmlang.intellijplugin.features;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ElmTemplateContext extends TemplateContextType {
    protected ElmTemplateContext() {
        super("ELM", "Elm");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        return file.getName().endsWith(".elm");
    }
}
