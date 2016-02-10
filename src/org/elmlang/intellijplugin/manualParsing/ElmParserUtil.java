package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

import static org.elmlang.intellijplugin.psi.ElmTypes.*;


public class ElmParserUtil extends GeneratedParserUtilBase {

    // case_of_header case_of_branch (case_of_one_or_more_separation case_of_branch)* case_of_trailing_separation
    public static boolean parseCaseOf(PsiBuilder builder, int level, Parser header, Parser branch, Parser oneOrMoreSeparations, Parser trailingSeparations) {
        if (!recursion_guard_(builder, level, "parseCaseOf")) return false;
        if (!nextTokenIs(builder, CASE)) return false;
        boolean result;
        PsiBuilder.Marker marker = enter_section_(builder);
        result = header.parse(builder, level + 1);
        builder.setTokenTypeRemapper(new IndentationTokenTypeRemapper(builder));
        result = result && branch.parse(builder, level + 1);
        result = result && separatedBranches(builder, level + 1, branch, oneOrMoreSeparations);
        result = result && trailingSeparations.parse(builder, level + 1);
        exit_section_(builder, marker, CASE_OF, result);
        builder.setTokenTypeRemapper(null);
        return result;
    }

    // (case_of_one_or_more_separation case_of_branch)*
    private static boolean separatedBranches(PsiBuilder builder, int level, Parser branch, Parser oneOrMoreSeparations) {
        if (!recursion_guard_(builder, level, "separatedBranches")) return false;
        int currentPosition = current_position_(builder);
        while (true) {
            if (!separatedBranch(builder, level + 1, branch, oneOrMoreSeparations)) break;
            if (!empty_element_parsed_guard_(builder, "separatedBranches", currentPosition)) break;
            currentPosition = current_position_(builder);
        }
        return true;
    }

    // case_of_one_or_more_separation case_of_branch
    private static boolean separatedBranch(PsiBuilder builder, int level, Parser branch, Parser oneOrMoreSeparations) {
        if (!recursion_guard_(builder, level, "separatedBranch")) return false;
        boolean result;
        PsiBuilder.Marker marker = enter_section_(builder);
        result = oneOrMoreSeparations.parse(builder, level + 1);
        result = result && branch.parse(builder, level + 1);
        exit_section_(builder, marker, null, result);
        return result;
    }
}
