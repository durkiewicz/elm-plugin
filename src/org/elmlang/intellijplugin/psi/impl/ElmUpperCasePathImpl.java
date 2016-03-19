package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import org.elmlang.intellijplugin.psi.ElmUpperCasePath;

public class ElmUpperCasePathImpl extends ElmPathBase implements ElmUpperCasePath {
    public ElmUpperCasePathImpl(ASTNode node) {
        super(node);
    }
}
