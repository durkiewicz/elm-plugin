package org.elmlang.intellijplugin.manualParsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

import java.util.EnumSet;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.consumeToken;
import static com.intellij.lang.parser.GeneratedParserUtilBase.exit_section_;
import static org.elmlang.intellijplugin.psi.ElmTypes.IN;
import static org.elmlang.intellijplugin.psi.ElmTypes.LET;
import static org.elmlang.intellijplugin.psi.ElmTypes.LET_IN;
import static org.elmlang.intellijplugin.manualParsing.IndentationTokenTypeRemapper.IndentationType;

public class LetInParser implements GeneratedParserUtilBase.Parser {
    private final GeneratedParserUtilBase.Parser innerValueDeclaration;
    private final GeneratedParserUtilBase.Parser otherValueDeclarations;
    private final GeneratedParserUtilBase.Parser expression;

    public LetInParser(
            Parser innerValueDeclaration, Parser otherValueDeclarations, Parser expression) {
        this.innerValueDeclaration = innerValueDeclaration;
        this.otherValueDeclarations = otherValueDeclarations;
        this.expression = expression;
    }

    @Override
    public boolean parse(PsiBuilder builder, int level) {
        if (!recursion_guard_(builder, level, "let_in")) return false;
        if (!nextTokenIs(builder, LET)) return false;
        boolean result;
        int indentationValue = 0;
        PsiBuilder.Marker marker = enter_section_(builder);
        result = consumeToken(builder, LET);
        IndentationTokenTypeRemapper reMapper = IndentationTokenTypeRemapper.getInstance();
        builder.setTokenTypeRemapper(reMapper);
        if (result) {
            indentationValue = IndentationHelper.getIndentationOfPreviousToken(builder);
            reMapper.pushIndentation(indentationValue, IndentationType.LET_IN);
        }
        result = result && this.innerValueDeclaration.parse(builder, level + 1);
        result = result && this.otherValueDeclarations.parse(builder, level + 1);
        result = result && consumeToken(builder, IN);
        reMapper.popIndentation(
                indentationValue,
                EnumSet.of(IndentationType.CASE_OF, IndentationType.LET_IN)
        );
        result = result && this.expression.parse(builder, level + 1);
        exit_section_(builder, marker, LET_IN, result);
        return result;
    }
}
