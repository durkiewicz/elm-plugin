package org.elmlang.intellijplugin;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.elmlang.intellijplugin.manualParsing.ElmManualPsiElementFactory;
import org.elmlang.intellijplugin.parser.ElmParser;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.elmlang.intellijplugin.psi.ElmTypes;
import org.jetbrains.annotations.NotNull;

public class ElmParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(
            ElmTypes.LINE_COMMENT,
            ElmTypes.START_COMMENT,
            ElmTypes.END_COMMENT,
            ElmTypes.COMMENT_CONTENT
        );

    public static final IFileElementType FILE = new IFileElementType(Language.<ElmLanguage>findInstance(ElmLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new ElmLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new ElmParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new ElmFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        PsiElement element = ElmManualPsiElementFactory.createElement(node);
        return element == null ? ElmTypes.Factory.createElement(node) : element;
    }
}