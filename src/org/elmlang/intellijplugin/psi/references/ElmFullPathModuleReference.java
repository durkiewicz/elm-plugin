package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmUpperCasePath;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

public class ElmFullPathModuleReference extends ElmReferenceBase<ElmUpperCasePath> {
    public ElmFullPathModuleReference(ElmUpperCasePath element) {
        super(element);
    }

    private ElmFullPathModuleReference(PsiElement element, ElmUpperCasePath referencingElement, TextRange rangeInElement) {
        super(element, referencingElement, rangeInElement);
    }

    @Override
    protected Function3<PsiElement, ElmUpperCasePath, TextRange, ElmReference> constructor() {
        return ElmFullPathModuleReference::new;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String moduleName = this.referencingElement.getText();
        return this.resolveUsingModuleIndex(moduleName, ElmFile::getModuleDeclaration);
    }
}
