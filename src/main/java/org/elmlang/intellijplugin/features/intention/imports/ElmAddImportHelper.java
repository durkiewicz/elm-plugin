package org.elmlang.intellijplugin.features.intention.imports;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.utils.ListUtils;
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
        Optional<ElmExposingClause> exposing1 = Optional.ofNullable(import1.getExposingClause());
        Optional<ElmExposingClause> exposing2 = Optional.ofNullable(import2.getExposingClause());

        // merge and sort each import's exposing clauses
        List<String> exposedNames =
                Stream.concat(
                        mergeExposedValues(exposing1, exposing2),
                        mergeExposedTypes(exposing1, exposing2)
                )
                .sorted(String::compareTo)
                .collect(Collectors.toList());

        // generate the new, merged import statement
        String moduleName = import1.getModuleName().getText();
        String modulePlusAlias = moduleName + mergeAliasClause(import1, import2);
        return ElmElementFactory.createImportExposing(project, modulePlusAlias, exposedNames);
    }

    private static String mergeAliasClause(ElmImportClause import1, ElmImportClause import2) {
        Optional<ElmAsClause> as1 = Optional.ofNullable(import1.getAsClause());
        Optional<ElmAsClause> as2 = Optional.ofNullable(import2.getAsClause());
        return Optional.ofNullable(as1.orElse(as2.orElse(null)))
                .map(e -> " as " + e.getUpperCaseId().getName())
                .orElse("");
    }

    private static Stream<String> mergeExposedValues(Optional<ElmExposingClause> exposing1, Optional<ElmExposingClause> exposing2) {
        return Stream.concat(
                    exposing1.map(e -> e.getLowerCaseIdList().stream()).orElse(Stream.empty()),
                    exposing2.map(e -> e.getLowerCaseIdList().stream()).orElse(Stream.empty())
               )
               .sorted((e1, e2) -> e1.getText().compareTo(e2.getText()))
               .map(PsiElement::getText);
    }

    private static Stream<String> mergeExposedTypes(Optional<ElmExposingClause> exposing1, Optional<ElmExposingClause> exposing2) {
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

        return exposedUnionsByName.entrySet()
                .stream()
                .map(entry -> mergeExposedUnionConstructors(entry.getKey(), entry.getValue()));
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
                .map(e -> e.getExposedUnionConstructors())
                .filter(e -> e != null)
                .flatMap(e -> e.getUpperCaseIdList().stream())
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
        List<ElmImportClause> existingImportClauses = sourceFile.getImportClauses();
        return existingImportClauses.isEmpty()
                ? prepareInsertInNewSection(sourceFile, project)
                : getSortedInsertPosition(moduleName, existingImportClauses);
    }

    private static ASTNode prepareInsertInNewSection(ElmFile sourceFile, Project project) {
        PsiElement moduleDecl = sourceFile.getModuleDeclaration().orElse(null);

        if (moduleDecl == null) {
            // source file does not have an explicit module declaration
            // so just insert at the front of the file.
            return sourceFile.getNode().getFirstChildNode();
        } else {
            // skip over comment blocks
            PsiElement nextNonCommentSibling = PsiTreeUtil.skipSiblingsForward(moduleDecl, PsiComment.class);

            // insert blanklines flanking the new import section
            ASTNode newFreshline = ElmElementFactory.createFreshLine(project).getNode();
            sourceFile.getNode().addChild(newFreshline, nextNonCommentSibling.getNode());
            ASTNode newFreshline2 = ElmElementFactory.createFreshLine(project).getNode();
            sourceFile.getNode().addChild(newFreshline2, newFreshline);
            return newFreshline.getTreeNext();
        }
    }

    private static int compareImportAndModule(ElmImportClause importClause, String moduleName) {
        return importClause.getModuleName().getText().compareTo(moduleName);
    }

    private static ASTNode getSortedInsertPosition(String moduleName, List<ElmImportClause> existingImportClauses) {
        // NOTE: we *assume* that the imports are already sorted and we
        // do not make any distinction between import groups/sections
        // (e.g. the practice of putting core libs in the first group,
        // 3rd party libs in a second group, and project files in the
        // final group). In the future we will likely want to revisit
        // this shortcut as it would be very frustrating for a developer
        // who "curates" their import list to keep fighting where the
        // quick-fix puts its imports.

        ElmImportClause firstImport = existingImportClauses.get(0);
        ElmImportClause lastImport = existingImportClauses.get(existingImportClauses.size() - 1);

        if (compareImportAndModule(firstImport, moduleName) >= 0) {
            return firstImport.getNode();
        } else if (compareImportAndModule(lastImport, moduleName) < 0) {
            return lastImport.getNode().getTreeNext();
        } else {
            // find the correct position somewhere in the middle
            return ListUtils
                    .zip(existingImportClauses, existingImportClauses.subList(1, existingImportClauses.size()))
                    .stream()
                    .filter(pair -> compareImportAndModule(pair.first, moduleName) < 0
                                 && compareImportAndModule(pair.second, moduleName) >= 0)
                    .map(pair -> pair.second.getNode())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("not found in the middle"));
        }
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