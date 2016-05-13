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
    private ElmFile file;

    private Stack<ElmUpperCaseId> types = new Stack<>();
    private Stack<ElmImportClause> importClauses = new Stack<>();
    private TypesProvidingPhase phase = CURRENT_FILE;

    ElmTypesProvider(ElmFile file) {
        this.file = file;
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
        ElmModuleIndex.getFilesByModuleName(moduleName, exposingClause.getProject())
                .stream()
                .findFirst()
                .ifPresent(f -> this.gatherTypesFromExposingClause(f, exposingClause));
    }

    private void gatherTypesFromExposingClause(ElmFile file, ElmExposingClause exposingClause) {
        TypeFilter filter = exposingClause.isExposingAll()
                ? TypeFilter.always(true)
                : exposingClause.getExposedTypeFilter();
        file.getExposedTypes(filter)
                .forEach(this.types::push);
    }

    private void updatePhase() {
        this.phase = this.importClauses.isEmpty() ? FINISHED : IMPORTED_FILES;
    }

    enum TypesProvidingPhase {
        CURRENT_FILE,
        IMPORTED_FILES,
        FINISHED
    }
}

