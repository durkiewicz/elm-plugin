package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import org.elmlang.intellijplugin.psi.ElmTypes;

public class ElmParserUtil extends GeneratedParserUtilBase {

    // case_of_header case_of_branch (case_of_one_or_more_separation case_of_branch)*
    public static boolean parseCaseOf(PsiBuilder builder, int level, Parser header, Parser branch, Parser oneOrMoreSeparations) {
        return new CaseOfParser(header, branch, oneOrMoreSeparations)
                .parse(builder, level);
    }

    // LET inner_value_declaration other_value_declarations IN expression
    public static boolean parseLetIn(PsiBuilder builder, int level, Parser innerValueDeclaration, Parser otherValueDeclarations, Parser expression) {
        return new LetInParser(innerValueDeclaration, otherValueDeclarations, expression)
                .parse(builder, level);
    }

    public static boolean parseUpperCasePath(PsiBuilder builder, int level) {
        return new PathParser(ElmTypes.UPPER_CASE_PATH, ElmTypes.UPPER_CASE_IDENTIFIER, null)
                .parse(builder, level);
    }

    public static boolean parseMixedCasePath(PsiBuilder builder, int level) {
        return new PathParser(ElmTypes.MIXED_CASE_PATH, ElmTypes.UPPER_CASE_IDENTIFIER, ElmTypes.LOWER_CASE_IDENTIFIER)
                .parse(builder, level);
    }

    public static boolean parseLowerCasePath(PsiBuilder builder, int level) {
        return new PathParser(ElmTypes.LOWER_CASE_PATH, ElmTypes.LOWER_CASE_IDENTIFIER, null)
                .parse(builder, level);
    }
}
