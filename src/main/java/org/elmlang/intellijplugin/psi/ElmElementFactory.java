package org.elmlang.intellijplugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.elmlang.intellijplugin.ElmFileType;
import org.elmlang.intellijplugin.utils.ListUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ElmElementFactory {
    @Nullable
    public static ElmUpperCaseId createUpperCaseId(Project project, String text) {
        final ElmFile file = createFile(project, String.format("type Uniq%s=%s", text, text));
        return Optional.ofNullable(file.getFirstChild())
                .filter(e -> e instanceof ElmTypeDeclaration)
                .flatMap(e -> ListUtils.head(((ElmTypeDeclaration) e).getUnionMemberList()))
                .map(ElmUnionMember::getUpperCaseId)
                .orElse(null);
    }

    @Nullable
    public static ElmLowerCaseId createLowerCaseId(Project project, String text) {
        final ElmFile file = createFile(project, String.format("%s=0", text));
        return Optional.ofNullable(file.getFirstChild())
                .filter(e -> e instanceof ElmValueDeclaration)
                .flatMap(e -> Optional.ofNullable(((ElmValueDeclaration)e).getPattern()))
                .flatMap(e -> ListUtils.head(e.getLowerCaseIdList()))
                .orElse(null);
    }

    @Nullable
    public static ElmImportClause createImport(Project project, String moduleName) {
        final ElmFile file = createFile(project, String.format("import %s", moduleName));
        return Optional.ofNullable(file.getFirstChild())
                .filter(e -> e instanceof ElmImportClause)
                .map(e -> (ElmImportClause) e)
                .orElse(null);
    }

    @Nullable
    public static ElmImportClause createImportExposing(Project project, String moduleName, String valueName) {
        return createImportExposing(project, moduleName, Arrays.asList(valueName));
    }

    @Nullable
    public static ElmImportClause createImportExposing(Project project, String moduleName, List<String> exposedNames) {
        String contents = String.join(", ", exposedNames);
        final ElmFile file = createFile(project, String.format("import %s exposing (%s)", moduleName, contents));
        return Optional.ofNullable(file.getFirstChild())
                .filter(e -> e instanceof ElmImportClause)
                .map(e -> (ElmImportClause) e)
                .orElse(null);
    }

    @Nullable
    public static PsiElement createFreshLine(Project project) {
        final ElmFile file = createFile(project, "\n");
        return Optional.ofNullable(file.getFirstChild())
                .filter(e -> e.getNode().getElementType() == ElmTypes.FRESH_LINE)
                .orElse(null);
    }

    private static ElmFile createFile(Project project, String text) {
        String name = "Dummy.elm";
        return (ElmFile) PsiFileFactory.getInstance(project)
                .createFileFromText(name, ElmFileType.INSTANCE, text);
    }
}