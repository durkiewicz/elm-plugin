package org.elmlang.intellijplugin.features.intention.imports;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBList;
import com.intellij.util.IncorrectOperationException;
import org.elmlang.intellijplugin.ElmModuleIndex;
import org.elmlang.intellijplugin.psi.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElmImportQuickFix implements IntentionAction {

    private String referenceNameToFix;

    public ElmImportQuickFix(String referenceNameToFix) {
        this.referenceNameToFix = referenceNameToFix;
    }

    @Override
    public String getText() {
        return "Add Import";
    }

    @Override
    public String getFamilyName() {
        return "style";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        List<String> refComponents = Arrays.asList(referenceNameToFix.split(Pattern.quote(".")));

        List<ElmImportCandidate> candidates = findCandidates(project, refComponents);
        if (candidates.isEmpty()) {
            HintManager.getInstance().showErrorHint(editor, "No module exporting '" + referenceNameToFix + "' found");
        } else if (candidates.size() == 1) {
            ElmImportCandidate candidate = candidates.get(0);
            fixWithCandidate(project, (ElmFile) file, refComponents, candidate);
        } else {
            List<ElmImportCandidate> sortedCandidates = new ArrayList<>(candidates);
            sortedCandidates.sort((a,b) -> a.moduleName.compareTo(b.moduleName));
            promptToSelectCandidate(project, (ElmFile) file, refComponents, sortedCandidates);
        }
    }

    private List<ElmImportCandidate> findCandidates(@NotNull Project project, List<String>refComponents) {
        String refBareName;
        Stream<String> moduleNames;

        if (refComponents.size() > 1) {
            refBareName = refComponents.get(refComponents.size()-1);
            String refQualifiedModuleName = String.join(".", refComponents.subList(0, refComponents.size()-1));
            moduleNames = Arrays.asList(refQualifiedModuleName).stream();
        } else {
            refBareName = referenceNameToFix;
            moduleNames = ElmModuleIndex.getAllModuleNames(project).stream();
        }

        final String refBareNameFinal = refBareName;

        if (refBareName.matches("^[A-Z].*")) {
            // it's an upper-case type or type constructor that we're looking for
            return moduleNames
                    .flatMap(name -> ElmModuleIndex.getFilesByModuleName(name, project).stream())
                    .map(f -> f.getExposedType(refBareNameFinal))
                    .filter(Optional::isPresent)
                    .map(e -> {
                        ElmUpperCaseId value = e.get();
                        ElmFile module = (ElmFile) value.getContainingFile();
                        String nameForImport = value.getName();
                        if (value.getParent() instanceof  ElmUnionMember) {
                            ElmTypeDeclaration typeDecl = (ElmTypeDeclaration) value.getParent().getParent();
                            String typeName = typeDecl.getUpperCaseId().getText();
                            nameForImport = typeName + "(" + value.getName() + ")";
                        }
                        return new ElmImportCandidate(
                                module.getModuleName(),
                                value.getName(),
                                nameForImport,
                                value
                        );
                    })
                    .collect(Collectors.toList());
        } else {
            // it's a lower-case value that we're looking for
            return moduleNames
                    .flatMap(name -> ElmModuleIndex.getFilesByModuleName(name, project).stream())
                    .map(f -> f.getExposedValueByName(refBareNameFinal))
                    .filter(Optional::isPresent)
                    .map(e -> {
                        ElmLowerCaseId value = e.get();
                        ElmFile module = (ElmFile) value.getContainingFile();
                        String nameForImport = value.getName();
                        return new ElmImportCandidate(
                                module.getModuleName(),
                                value.getName(),
                                nameForImport,
                                value
                        );
                    })
                    .collect(Collectors.toList());
        }
    }

    private void promptToSelectCandidate(@NotNull final Project project, final ElmFile file, final List<String> refComponents, List<ElmImportCandidate> candidates) {
        final JBList list = new JBList(candidates);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                @SuppressWarnings("unchecked") ElmImportCandidate candidate = (ElmImportCandidate)value;
                setText(candidate.moduleName);
                return result;
            }
        });
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        JBPopupFactory.getInstance().createListPopupBuilder(list)
                .setTitle("Import from module:")
                .setItemChoosenCallback(() -> {
                    final Object value = list.getSelectedValue();
                    if (value instanceof ElmImportCandidate) {
                        ElmImportCandidate candidate = (ElmImportCandidate) value;
                        fixWithCandidate(project, file, refComponents, candidate);
                    }
                })
                .setFilteringEnabled(value -> ((ElmImportCandidate)value).moduleName)
                .createPopup().showInBestPositionFor(editor);
    }

    private void fixWithCandidate(@NotNull final Project project, final ElmFile file, final List<String> refComponents, final ElmImportCandidate candidate) {
        new WriteCommandAction.Simple(project) {
            @Override
            protected void run() throws Throwable {
                boolean importAsQualified = refComponents.size() > 1;
                ElmAddImportHelper.addOrUpdateImport(file, candidate.moduleName, candidate.nameForImport, importAsQualified);
            }
        }.execute();
    }
}
