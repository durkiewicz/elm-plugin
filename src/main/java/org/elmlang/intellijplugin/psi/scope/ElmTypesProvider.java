package org.elmlang.intellijplugin.psi.scope;

import com.intellij.psi.PsiElement;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.utils.TypeFilter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;

import static org.elmlang.intellijplugin.psi.scope.ElmTypesProvider.TypesProvidingPhase.*;


class ElmTypesProvider {
    private final ElmFile file;

    private final Stack<ElmUpperCaseId> types = new Stack<>();
    private final Stack<ElmImportClause> importClauses = new Stack<>();
    private final Stack<String> implicitImports;
    private TypesProvidingPhase phase = CURRENT_FILE;

    ElmTypesProvider(ElmFile file) {
        this.file = file;
        this.implicitImports = ElmCoreLibrary.getImplicitImportsCopy();
    }

    Optional<ElmUpperCaseId> nextType() {
        if (!this.types.isEmpty()) {
            return Optional.of(types.pop());
        }

        switch (this.phase) {
            case CURRENT_FILE:
                gatherTypesFromCurrentFile();
                return this.nextType();
            case IMPORTED_FILES:
                gatherTypesFromImport();
                return this.nextType();
            case IMPLICIT_IMPORTS:
                gatherTypesFromImplicitImport();
                return this.nextType();
            case FINISHED:
                return Optional.empty();
            default:
                throw new RuntimeException("Unhandled phase " + this.phase);
        }
    }

    private void gatherTypesFromCurrentFile() {
        this.file.getInternalTypes()
                .forEach(this.types::push);
        Arrays.stream(this.file.getChildren())
                .forEach(this::gatherImportClause);
        this.updatePhase();
    }

    private void gatherImportClause(PsiElement element) {
        if (element instanceof ElmImportClause) {
            this.importClauses.push((ElmImportClause) element);
        }
    }

    private void gatherTypesFromImport() {
        ElmImportClause importClause = this.importClauses.pop();
        Optional.ofNullable(importClause.getExposingClause())
                .ifPresent(exposingClause -> gatherTypesFromExposingClause(importClause.getModuleName().getText(), exposingClause));
        updatePhase();
    }

    private void gatherTypesFromExposingClause(String moduleName, ElmExposingClause exposingClause) {
        TypeFilter filter = exposingClause.isExposingAll()
                ? TypeFilter.always(true)
                : exposingClause.getExposedTypeFilter();
        this.gatherTypesFromFile(moduleName, filter);
    }

    private void gatherTypesFromImplicitImport() {
        String module = this.implicitImports.pop();
        this.gatherTypesFromFile(module, TypeFilter.always(true));
        updatePhase();
    }

    private void gatherTypesFromFile(String moduleName, TypeFilter filter) {
        ElmModuleIndex.getFilesByModuleName(moduleName, this.file.getProject())
                .stream()
                .findFirst()
                .ifPresent(f -> this.gatherTypesFromFile(f, filter));
    }

    private void gatherTypesFromFile(ElmFile file, TypeFilter filter) {
        file.getExposedTypes(filter)
                .forEach(this.types::push);
    }

    private void updatePhase() {
        this.phase = this.implicitImports.isEmpty()
                ? FINISHED
                : this.importClauses.isEmpty()
                ? IMPLICIT_IMPORTS
                : IMPORTED_FILES;
    }

    enum TypesProvidingPhase {
        CURRENT_FILE,
        IMPORTED_FILES,
        IMPLICIT_IMPORTS,
        FINISHED
    }
}

