package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.exit_section_;
import static org.elmlang.intellijplugin.psi.ElmTypes.DOT;
import static org.elmlang.intellijplugin.psi.ElmTypes.FIELD_ACCESS;
import static org.elmlang.intellijplugin.psi.ElmTypes.LOWER_CASE_IDENTIFIER;

public class FieldAccessParser implements GeneratedParserUtilBase.Parser {
    @Override
    public boolean parse(PsiBuilder builder, int level) {
        if (!recursion_guard_(builder, level, "field_access")) return false;
        if (builder.rawLookup(0) != DOT
                || builder.rawLookup(1) != LOWER_CASE_IDENTIFIER
                || builder.rawLookup(2) == DOT)
            return false;
        boolean result;
        PsiBuilder.Marker marker = enter_section_(builder);
        result = consumeTokens(builder, 0, DOT, LOWER_CASE_IDENTIFIER);
        exit_section_(builder, marker, FIELD_ACCESS, result);
        return result;
    }
}
