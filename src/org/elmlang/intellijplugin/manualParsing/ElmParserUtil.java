package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

import static org.elmlang.intellijplugin.psi.ElmTypes.CASE;
import static org.elmlang.intellijplugin.psi.ElmTypes.CASE_OF;
import static org.elmlang.intellijplugin.psi.ElmTypes.OF;


public class ElmParserUtil extends GeneratedParserUtilBase {

    public static boolean parse_case_of(PsiBuilder builder, int level, GeneratedParserUtilBase.Parser expression) {
        if (!recursion_guard_(builder, level, "case_of")) return false;
        if (!nextTokenIs(builder, CASE)) return false;
        boolean result;
        PsiBuilder.Marker marker = enter_section_(builder);
        result = consumeToken(builder, CASE);
        result = result && expression.parse(builder, level + 1);
        result = result && consumeToken(builder, OF);
        exit_section_(builder, marker, CASE_OF, result);
        return result;
    }
}
