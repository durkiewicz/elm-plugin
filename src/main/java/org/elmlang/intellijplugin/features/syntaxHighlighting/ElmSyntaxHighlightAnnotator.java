package org.elmlang.intellijplugin.features.syntaxHighlighting;

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

import static org.elmlang.intellijplugin.features.syntaxHighlighting.ElmSyntaxHighlighter.*;


public class ElmSyntaxHighlightAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof ElmValueDeclaration) {
            highlightValueDeclaration(holder, (ElmValueDeclaration) element);
        } else if (element instanceof ElmTypeAnnotation) {
            highlightTypeAnnotation(holder, (ElmTypeAnnotation) element);
        } else if (isTypeElement(element)) {
            highlightElement(holder, element, ELM_TYPE);
        }
    }

    private static boolean isTypeElement(@NotNull PsiElement element) {
        return element instanceof ElmUpperCaseId
                && PsiTreeUtil.getParentOfType(
                        element,
                        ElmTypeAnnotation.class,
                        ElmModuleDeclaration.class,
                        ElmImportClause.class) == null;
    }

    private static void highlightValueDeclaration(@NotNull AnnotationHolder holder, @NotNull ElmValueDeclaration declaration) {
        // First try treating it as a function declaration
        ElmLowerCaseId nameElement = Optional.ofNullable(declaration.getFunctionDeclarationLeft())
                .map(ElmFunctionDeclarationLeft::getLowerCaseId)
                // Fallback to a generic (null-ary) value declaration
                .orElseGet(() ->
                        Optional.ofNullable(declaration.getPattern())
                                .flatMap(p -> ListUtils.head(p.getLowerCaseIdList()))
                                .orElse(null)
                );

        if (nameElement != null) {
            highlightElement(holder, nameElement, ELM_DEFINITION_NAME);
        }
    }

    private static void highlightTypeAnnotation(@NotNull AnnotationHolder holder, @NotNull ElmTypeAnnotation typeAnnotation) {
        Optional.ofNullable(typeAnnotation.getLowerCaseId())
                .ifPresent(e -> highlightElement(holder, e, ELM_TYPE_ANNOTATION_NAME));

        ElmTypeDefinition typeDefinition = typeAnnotation.getTypeDefinition();
        Stream.concat(
                PsiTreeUtil.findChildrenOfType(typeDefinition, ElmLowerCaseId.class).stream(),
                PsiTreeUtil.findChildrenOfType(typeDefinition, ElmUpperCaseId.class).stream())
                .forEach(elt -> highlightElement(holder, elt, ELM_TYPE_ANNOTATION_SIGNATURE_TYPES));
    }

    private static void highlightElement(@NotNull AnnotationHolder holder, @NotNull PsiElement element, TextAttributesKey key) {
        Annotation annotation = holder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(key);
    }
}
