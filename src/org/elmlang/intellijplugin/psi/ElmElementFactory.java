package org.elmlang.intellijplugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import org.elmlang.intellijplugin.ElmFileType;

public class ElmElementFactory {
//    public static SimpleProperty createProperty(Project project, String name, String value) {
//        final SimpleFile file = createFile(project, name + " = " + value);
//        return (SimpleProperty) file.getFirstChild();
//    }
//
//    public static SimpleProperty createProperty(Project project, String name) {
//        final SimpleFile file = createFile(project, name);
//        return (SimpleProperty) file.getFirstChild();
//    }
//
//    public static PsiElement createCRLF(Project project) {
//        final SimpleFile file = createFile(project, "\n");
//        return file.getFirstChild();
//    }

    public static ElmUpperCaseId createUpperCaseId(Project project, String text) {
        final ElmFile file = createFile(project, String.format("type Uniq%s=%s", text, text));
        return (ElmUpperCaseId) file.getFirstChild().getLastChild().getFirstChild();
    }

    public static ElmLowerCaseId createLowerCaseId(Project project, String text) {
        final ElmFile file = createFile(project, String.format("%s=0", text));
        return (ElmLowerCaseId) file.getFirstChild().getFirstChild();
    }

    private static ElmFile createFile(Project project, String text) {
        String name = "Dummy.elm";
        return (ElmFile) PsiFileFactory.getInstance(project)
                .createFileFromText(name, ElmFileType.INSTANCE, text);
    }
}