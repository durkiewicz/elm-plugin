package org.elmlang.intellijplugin.features.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.PsiNamedElement;
import org.elmlang.intellijplugin.ElmIcons;
import org.elmlang.intellijplugin.psi.ElmFunctionDeclarationLeft;
import org.elmlang.intellijplugin.psi.ElmPattern;
import org.elmlang.intellijplugin.psi.ElmValueDeclarationBase;
import org.elmlang.intellijplugin.utils.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ElmValueDeclarationTreeElement extends PsiTreeElementBase<ElmValueDeclarationBase> {
    ElmValueDeclarationTreeElement(ElmValueDeclarationBase psiElement) {
        super(psiElement);
    }

    @Override
    public Icon getIcon(boolean open) {
        return Optional.ofNullable(getElement())
                .map(e -> {
                    switch (getRole(e)) {
                        case VALUE: return ElmIcons.VALUE;
                        case FUNCTION: return ElmIcons.FUNCTION;
                        default: return null;
                    }
                }).orElse(ElmIcons.FILE);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return Optional.ofNullable(getElement())
                .map(this::getName)
                .orElse("");
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    // TODO [kl] where is the right place to put this logic? a mixin and injected method?
    enum Role {
        VALUE,
        FUNCTION
    }

    private Role getRole(ElmValueDeclarationBase declaration) {
        return Optional.ofNullable(declaration.getFunctionDeclarationLeft())
                .map(e -> e.getPatternList().isEmpty() ? Role.VALUE : Role.FUNCTION)
                .orElse(Role.VALUE);
    }

    private String getName(ElmValueDeclarationBase declaration) {
        switch (getRole(declaration)) {
            case VALUE: {
                return Optional.ofNullable(declaration.getPattern())
                        .map(ElmPattern::getLowerCaseIdList)
                        .flatMap(ListUtils::head)
                        .map(PsiNamedElement::getName)
                        .orElse("unknown value");
            }
            case FUNCTION: {
                return Optional.ofNullable(declaration.getFunctionDeclarationLeft())
                        .map(ElmFunctionDeclarationLeft::getLowerCaseId)
                        .map(PsiNamedElement::getName)
                        .orElse("unknown function");
            }
            default:
                return "unknown decl role";
        }
    }
}
