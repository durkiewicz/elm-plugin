package org.elmlang.intellijplugin.features.intention.imports;

import com.intellij.psi.PsiElement;

public class ElmImportCandidate {
    public final String moduleName;
    public final String name;
    public final String nameForImport;
    public final PsiElement element;

    /**
     * @param moduleName    the module where this value/type lives
     * @param name          the name of the value/type
     * @param nameForImport the name suitable for insert into an exposing clause.
     *                      Typically this is the same as `name`, but when importing
     *                      a bare union type member, it will be the parenthesized
     *                      form: "TypeName(MemberName)"
     * @param element       the value/type element in the module-to-be-imported
     */
    public ElmImportCandidate(String moduleName, String name, String nameForImport, PsiElement element) {
        this.moduleName = moduleName;
        this.name = name;
        this.nameForImport = nameForImport;
        this.element = element;
    }

    @Override
    public String toString() {
        return "ElmImportCandidate{" +
                "moduleName='" + moduleName + '\'' +
                ", name='" + name + '\'' +
                ", nameForImport='" + nameForImport + '\'' +
                ", element=" + element +
                '}';
    }
}
