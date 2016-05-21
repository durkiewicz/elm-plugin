package org.elmlang.intellijplugin.features;

import com.intellij.lang.Commenter;

public class ElmCommenter implements Commenter {

    @Override
    public String getLineCommentPrefix() {
        return "--";
    }

    @Override
    public String getBlockCommentPrefix() {
        return "{-";
    }

    @Override
    public String getBlockCommentSuffix() {
        return "-}";
    }

    @Override
    public String getCommentedBlockCommentPrefix() {
        return "{-";
    }

    @Override
    public String getCommentedBlockCommentSuffix() {
        return "-}";
    }
}
