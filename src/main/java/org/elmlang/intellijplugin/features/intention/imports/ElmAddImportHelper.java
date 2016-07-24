package org.elmlang.intellijplugin.features.intention.imports;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElmAddImportHelper {
    private ElmAddImportHelper() {
    }

    public static void addOrUpdateImport(ElmFile sourceFile, String moduleName, @Nullable String nameToImport, boolean importAsQualified) {
        Project project = sourceFile.getProject();

        // Create a new, stand-alone import based solely on the quick-fix
        ElmImportClause newImport = importAsQualified
                ? ElmElementFactory.createImport(project, moduleName)
                : ElmElementFactory.createImportExposing(project, moduleName, nameToImport);

        // If there are any existing imports for the same module, merge with it
        Optional<ElmImportClause> existingImportClause = sourceFile.getImportClauseByModuleName(moduleName);
        if (existingImportClause.isPresent()) {
            // merge with existing import
            ElmImportClause oldImport = existingImportClause.get();
            ElmImportClause mergedImport = mergeImports(sourceFile, oldImport, newImport);
            oldImport.replace(mergedImport);
        } else {
            // insert a new import clause
            ASTNode insertPosition = getInsertPosition(sourceFile, moduleName);
            insertImportClause(newImport, insertPosition);
        }
    }

    private static ElmImportClause mergeImports(ElmFile sourceFile, ElmImportClause import1, ElmImportClause import2) {
        assert Objects.equals(import1.getModuleName().getText(),
                              import2.getModuleName().getText());

        Project project = sourceFile.getProject();

        // If either one of the imports has an alias clause, extract it
        // for use in the new, merged import.
        Optional<ElmAsClause> as1 = Optional.ofNullable(import1.getAsClause());
        Optional<ElmAsClause> as2 = Optional.ofNullable(import2.getAsClause());
        String aliasText = Optional.ofNullable(as1.orElse(as2.orElse(null)))
                .map(e -> " as " + e.getUpperCaseId().getName())
                .orElse("");

        // Merge the exposing clause(s)
        Optional<ElmExposingClause> exposing1 = Optional.ofNullable(import1.getExposingClause());
        Optional<ElmExposingClause> exposing2 = Optional.ofNullable(import2.getExposingClause());

        List<String> exposedValues =
                Stream.concat(
                        exposing1.map(e -> e.getLowerCaseIdList().stream()).orElse(Stream.empty()),
                        exposing2.map(e -> e.getLowerCaseIdList().stream()).orElse(Stream.empty())
                )
                .sorted((e1, e2) -> e1.getText().compareTo(e2.getText()))
                .map(PsiElement::getText)
                .collect(Collectors.toList());

        Map<String,List<ElmExposedUnion>> exposedUnionsByName =
                Stream.concat(
                        exposing1.map(e -> e.getExposedUnionList().stream()).orElse(Stream.empty()),
                        exposing2.map(e -> e.getExposedUnionList().stream()).orElse(Stream.empty())
                )
                .collect(
                    Collectors.groupingBy(
                        e -> e.getUpperCaseId().getName()
                    )
                );

        List<String> exposedTypes = exposedUnionsByName.entrySet()
                .stream()
                .map(entry -> mergeExposedUnionConstructors(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // combine the new exposing list and sort it
        List<String> exposedNames = new ArrayList<>();
        exposedNames.addAll(exposedValues);
        exposedNames.addAll(exposedTypes);
        exposedNames.sort(String::compareTo);

        // generate the new, merged import statement
        String moduleName = import1.getModuleName().getText();
        String modulePlusAlias = moduleName + aliasText;
        return ElmElementFactory.createImportExposing(project, modulePlusAlias, exposedNames);
    }

    private static String mergeExposedUnionConstructors(String typeName, List<ElmExposedUnion> unions) {
        boolean exposingAll = unions.stream().anyMatch(union -> {
            ElmExposedUnionConstructors ctors = union.getExposedUnionConstructors();
            return ctors != null && ctors.isExposingAll();
        }) ;

        if (exposingAll) {
            return typeName + "(..)";
        }

        String body;
        body = unions.stream()
                .filter(e -> e.getExposedUnionConstructors() != null)
                .flatMap(e -> e.getExposedUnionConstructors().getUpperCaseIdList().stream())
                .sorted((e1, e2) -> {
                    String type1 = e1.getText();
                    String type2 = e2.getText();
                    return type1.compareTo(type2);
                })
                .map(e -> e.getName())
                .collect(Collectors.joining(", "));

        return body.isEmpty() ? typeName : typeName + "(" + body + ")";
    }

    private static ASTNode getInsertPosition(ElmFile sourceFile, String moduleName) {
        Project project = sourceFile.getProject();
        ASTNode insertPosition = null;
        List<ElmImportClause> existingImportClauses = sourceFile.getImportClauses();
        if (existingImportClauses.isEmpty()) {
            // we need to create a new import section
            PsiElement moduleDecl = sourceFile.getModuleDeclaration().orElse(null);

            if (moduleDecl == null) {
                // source file does not have an explicit module declaration
                // so just insert at the front of the file.
                insertPosition = sourceFile.getNode().getFirstChildNode();
            } else {
                // skip over comment blocks
                PsiElement nextNonCommentSibling = PsiTreeUtil.skipSiblingsForward(moduleDecl, PsiComment.class);

                // insert blanklines flanking the new import section
                ASTNode newFreshline = ElmElementFactory.createFreshLine(project).getNode();
                sourceFile.getNode().addChild(newFreshline, nextNonCommentSibling.getNode());
                ASTNode newFreshline2 = ElmElementFactory.createFreshLine(project).getNode();
                sourceFile.getNode().addChild(newFreshline2, newFreshline);
                insertPosition = newFreshline.getTreeNext();
            }
        } else {
            // find the sorted position within the existing import section

            // NOTE: we *assume* that the imports are already sorted and we
            // do not make any distinction between import groups/sections
            // (e.g. the practice of putting core libs in the first group,
            // 3rd party libs in a second group, and project files in the
            // final group). In the future we will likely want to revisit
            // this shortcut as it would very frustrating for a developer
            // who "curates" their import list to keep fighting where the
            // quick-fix puts its imports.
            ElmImportClause prevImport = null;
            for (ElmImportClause currentImport : existingImportClauses) {
                String a = prevImport == null ? "" : prevImport.getModuleName().getText();
                String b = currentImport.getModuleName().getText();

                if (a.compareTo(moduleName) < 0 && moduleName.compareTo(b) <= 0) {
                    insertPosition = currentImport.getNode();
                    break;
                }
                prevImport = currentImport;
            }

            if (insertPosition == null) {
                // the new module belongs at the end of the list
                insertPosition = existingImportClauses.get(existingImportClauses.size() - 1).getNode().getTreeNext();
            }
        }
        return insertPosition;
    }

    private static void insertImportClause(ElmImportClause importClause, ASTNode insertPosition) {
        Project project = importClause.getProject();
        ASTNode parent = insertPosition.getTreeParent();
        ASTNode beforeInsertPosition = insertPosition.getTreePrev();

        // ensure that a freshline exists immediately following
        // where we are going to insert the new import clause.
        ASTNode prevFreshline = null;
        if (insertPosition.getElementType() != ElmTypes.FRESH_LINE) {
            prevFreshline = ElmElementFactory.createFreshLine(project).getNode();
            parent.addChild(prevFreshline, insertPosition);
        } else {
            prevFreshline = insertPosition;
        }

        // insert the import clause before the freshline
        parent.addChild(importClause.getNode(), prevFreshline);

        // ensure that freshline exists *before* the new import clause
        if (beforeInsertPosition != null && beforeInsertPosition.getElementType() != ElmTypes.FRESH_LINE) {
            ASTNode newFreshline = ElmElementFactory.createFreshLine(project).getNode();
            parent.addChild(newFreshline, importClause.getNode());
        }
    }
}