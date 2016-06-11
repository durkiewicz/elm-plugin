package org.elmlang.intellijplugin;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.elmlang.intellijplugin.psi.*;
import org.elmlang.intellijplugin.utils.ListUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

import static org.elmlang.intellijplugin.ElmSyntaxHighlighter.*;


public class ElmSyntaxHighlightAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

        if (element instanceof ElmValueDeclaration) {
            highlightValueDeclaration(holder, (ElmValueDeclaration) element);

        } else if (element instanceof ElmTypeAnnotation) {
            highlightTypeAnnotation(holder, (ElmTypeAnnotation) element);

        } else if (element instanceof ElmUpperCaseId
                && (PsiTreeUtil.getParentOfType(element, ElmTypeAnnotation.class,
                        ElmModuleDeclaration.class, ElmImportClause.class) == null)) {
            highlightElement(holder, element, ELM_TYPE);

        } else if (element instanceof ElmBacktickedFunction) {
            highlightElement(holder, element, ELM_OPERATOR);
        }
    }

    private void highlightValueDeclaration(@NotNull AnnotationHolder holder, @NotNull ElmValueDeclaration declaration) {
        // First try treating it as a function declaration
        ElmLowerCaseId nameElement = Optional.ofNullable(declaration.getFunctionDeclarationLeft())
                .map(ElmFunctionDeclarationLeft::getLowerCaseId)
                .orElse(null);

        // Fallback to a generic (null-ary) value declaration
        if (nameElement == null) {
            nameElement = Optional.ofNullable(declaration.getPattern())
                    .flatMap(p -> ListUtils.head(p.getLowerCaseIdList()))
                    .orElse(null);
        }

        if (nameElement != null) {
            highlightElement(holder, nameElement, ELM_DEFINITION_NAME);
        }
    }

    private void highlightTypeAnnotation(@NotNull AnnotationHolder holder, @NotNull ElmTypeAnnotation typeAnnotation) {
        Optional.ofNullable(typeAnnotation.getLowerCaseId())
                .ifPresent(e -> highlightElement(holder, e, ELM_TYPE_ANNOTATION_NAME));

        ElmTypeDefinition typeDefinition = typeAnnotation.getTypeDefinition();
        Stream.concat(
                PsiTreeUtil.findChildrenOfType(typeDefinition, ElmLowerCaseId.class).stream(),
                PsiTreeUtil.findChildrenOfType(typeDefinition, ElmUpperCaseId.class).stream())
                .forEach(elt -> highlightElement(holder, elt, ELM_TYPE_ANNOTATION_SIGNATURE_TYPES));
    }

    private void highlightElement(@NotNull AnnotationHolder holder, @NotNull PsiElement element, TextAttributesKey key) {
        Annotation annotation = holder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(key);
    }
}
