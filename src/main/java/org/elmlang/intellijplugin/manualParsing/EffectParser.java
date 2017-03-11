package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static org.elmlang.intellijplugin.psi.ElmTypes.*;

public class EffectParser implements GeneratedParserUtilBase.Parser {
    @Override
    public boolean parse(PsiBuilder builder, int level) {
        if (!recursion_guard_(builder, level, "EffectParser")) return false;
        if (builder.rawLookup(0) != LOWER_CASE_IDENTIFIER || !"effect".equals(builder.getTokenText()))
            return false;
        boolean result;
        PsiBuilder.Marker marker = enter_section_(builder);
        result = consumeTokens(builder, 0, LOWER_CASE_IDENTIFIER);
        exit_section_(builder, marker, EFFECT, result);
        return result;
    }
}