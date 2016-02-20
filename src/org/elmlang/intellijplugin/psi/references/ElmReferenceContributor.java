package org.elmlang.intellijplugin.psi.references;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.elmlang.intellijplugin.ElmLanguage;
import org.jetbrains.annotations.NotNull;

public class ElmReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement()
                        .withLanguage(ElmLanguage.INSTANCE),
                new ElmReferenceProvider()
        );

//        PsiElementPattern.Capture<PsiNamedElement> variableCapture =
//                PlatformPatterns.psiElement(PsiNamedElement.class)
////                        .withParent(HaskellVars.class)
////                        .withParent(HaskellGendecl.class)
//                        .withLanguage(ElmLanguage.INSTANCE);
//        registrar.registerReferenceProvider(
//                variableCapture,
//                new ElmReferenceProvider()
//        );
    }
}
