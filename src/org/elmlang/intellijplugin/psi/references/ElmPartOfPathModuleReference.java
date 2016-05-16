package org.elmlang.intellijplugin.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.scope.ElmCoreLibrary;
import org.elmlang.intellijplugin.utils.Function3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElmPartOfPathModuleReference extends ElmReferenceBase<PsiElement> {

    private final int modulePartLength;

    public ElmPartOfPathModuleReference(PsiElement element, TextRange textRange, int modulePartLength) {
        this(element, element, textRange, modulePartLength);
    }

    private ElmPartOfPathModuleReference(PsiElement element, PsiElement referencingElement, TextRange rangeInElement, int modulePartLength) {
        super(element, referencingElement, rangeInElement);
        this.modulePartLength = modulePartLength;
    }

    @Override
    protected Function3<PsiElement, PsiElement, TextRange, ElmReference> constructor() {
        return (element, path, textRange) -> new ElmPartOfPathModuleReference(element, path, textRange, this.modulePartLength);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        TextRange textRange = this.getRangeInElement();
        String moduleName = this.myElement.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        ElmFile file = (ElmFile) this.myElement.getContainingFile();
        if (this.modulePartLength == 1) {
            return file.getImportClauseByAlias(moduleName)
            .flatMap(e -> Optional.ofNullable(e.getAsClause()))
            .map(e -> (PsiElement)e.getUpperCaseId())
            .orElseGet(() -> resolveAsRealModule(moduleName, file));
        } else {
            return resolveAsRealModule(moduleName, file);
        }
    }

    @Override
    public ElmReferenceTarget getTarget() {
        return ElmReferenceTarget.MODULE;
    }

    @Nullable
    private PsiElement resolveAsRealModule(String moduleName, ElmFile file) {
        if (ElmCoreLibrary.isImplicitImport(moduleName) || file.getImportClauseByModuleName(moduleName).isPresent()) {
            return this.resolveUsingModuleIndex(moduleName, ElmFile::getModuleDeclaration);
        } else {
            return null;
        }
    }
}
