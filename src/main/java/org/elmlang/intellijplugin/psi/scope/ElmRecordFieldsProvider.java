package org.elmlang.intellijplugin.psi.scope;

import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmImportClause;
import org.elmlang.intellijplugin.psi.ElmLowerCaseId;

import java.util.Optional;
import java.util.Stack;

import static org.elmlang.intellijplugin.psi.scope.ElmTypesProvider.TypesProvidingPhase.*;

public class ElmRecordFieldsProvider {
    private final ElmFile file;

    private final Stack<ElmLowerCaseId> fields = new Stack<>();
    private final Stack<ElmImportClause> importClauses = new Stack<>();
    private final Stack<String> implicitImports;
    private ElmTypesProvider.TypesProvidingPhase phase = CURRENT_FILE;

    ElmRecordFieldsProvider(ElmFile file) {
        this.file = file;
        this.implicitImports = ElmCoreLibrary.getImplicitImportsCopy();
    }

    Optional<ElmLowerCaseId> nextField() {
        if (!this.fields.isEmpty()) {
            return Optional.of(this.fields.pop());
        }

        switch (this.phase) {
            case CURRENT_FILE:
                gatherFieldsFromCurrentFile();
                return this.nextField();
            case IMPORTED_FILES:
                gatherTypesFromImport();
                return this.nextField();
            case IMPLICIT_IMPORTS:
                gatherTypesFromImplicitImport();
                return this.nextField();
            case FINISHED:
                return Optional.empty();
            default:
                throw new RuntimeException("Unhandled phase " + this.phase);
        }
    }

    private void gatherFieldsFromCurrentFile() {
        this.file.getRecordFields()
                .forEach(this.fields::push);
        this.file.getImportClauses()
                .forEach(this.importClauses::push);
        this.updatePhase();
    }

    private void updatePhase() {
        this.phase = this.implicitImports.isEmpty()
                ? FINISHED
                : this.importClauses.isEmpty()
                ? IMPLICIT_IMPORTS
                : IMPORTED_FILES;
    }

    private void gatherTypesFromImport() {
        ElmImportClause importClause = this.importClauses.pop();
        this.gatherTypesFromFile(importClause.getModuleName().getText());
        updatePhase();
    }

    private void gatherTypesFromFile(String moduleName) {
        ElmModuleIndex.getFilesByModuleName(moduleName, this.file.getProject())
                .stream()
                .findFirst()
                .ifPresent(f -> f.getRecordFields().forEach(this.fields::push));
    }

    private void gatherTypesFromImplicitImport() {
        String module = this.implicitImports.pop();
        this.gatherTypesFromFile(module);
        updatePhase();
    }
}
