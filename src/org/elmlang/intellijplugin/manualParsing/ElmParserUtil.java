package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

public class ElmParserUtil extends GeneratedParserUtilBase {

    // case_of_header case_of_branch (case_of_one_or_more_separation case_of_branch)*
    public static boolean parseCaseOf(PsiBuilder builder, int level, Parser header, Parser branch, Parser oneOrMoreSeparations) {
        return new CaseOfParser(header, branch, oneOrMoreSeparations)
                .parse(builder, level);
    }
}
