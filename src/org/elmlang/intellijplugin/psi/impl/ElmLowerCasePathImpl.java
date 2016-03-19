package org.elmlang.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import org.elmlang.intellijplugin.psi.ElmLowerCasePath;

public class ElmLowerCasePathImpl extends ElmPathBase implements ElmLowerCasePath {
    public ElmLowerCasePathImpl(ASTNode node) {
        super(node);
    }
}
