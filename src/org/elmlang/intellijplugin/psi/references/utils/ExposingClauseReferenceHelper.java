package org.elmlang.intellijplugin.psi.references.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmImportClause;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class ExposingClauseReferenceHelper {
    @Nullable
    public static PsiElement resolveImported(PsiElement element, Function<ElmFile, PsiElement> resolveInFile) {
        return getImportAncestor(element)
                .map(ElmImportClause::getModuleName)
                .map(PsiElement::getText)
                .flatMap(m -> ElmModuleIndex.getFilesByModuleName(m, element.getProject()).stream().findFirst())
                .map(resolveInFile)
                .orElse(null);
    }

    @Nullable
    public static PsiElement resolveExposed(PsiElement element, Function<ElmFile, PsiElement> resolveInFile) {
        PsiFile file;
        try {
            file = element.getContainingFile();
        } catch (PsiInvalidElementAccessException ex) {
            return null;
        }
        return resolveExposed(file, resolveInFile);
    }

    private static PsiElement resolveExposed(PsiFile file, Function<ElmFile, PsiElement> resolveInFile) {
        return Optional.ofNullable(file)
                .filter(f -> f instanceof ElmFile)
                .map(f -> (ElmFile)f)
                .map(resolveInFile)
                .orElse(null);
    }

    private static Optional<ElmImportClause> getImportAncestor(PsiElement element) {
        PsiElement e = element.getParent();
        while (e != null) {
            if (e instanceof ElmImportClause) {
                return Optional.of((ElmImportClause) e);
            }
            e = e.getParent();
        }

        return Optional.empty();
    }
}
