package org.elmlang.intellijplugin.psi.scope;

import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.utils.TypeFilter;

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
        this.file.getImportClauses()
                .forEach(this.importClauses::push);
        this.updatePhase();
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
                .forEach(f -> this.gatherTypesFromFile(f, filter));
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

