package org.elmlang.intellijplugin.features.intention.imports;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ElmImportQuickFixTest extends LightPlatformCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData/intentions/import";
    }

    public void testBasic() {
        myFixture.configureByFiles("Basic.elm", "LibraryA.elm");

        // This code to find the 'quick fix' *should* work, but I get a runtime
        // assertion "Must not start highlighting from within write action, or deadlock is imminent".
        // So for the time being, I'm just going to new-up the quick fix myself
        //
        //        IntentionAction quickFix = myFixture.findSingleIntention("Add Import");

        ElmImportQuickFix fix = new ElmImportQuickFix("LibraryA.avril14th");
        myFixture.launchAction(fix);
        myFixture.checkResultByFile("Basic_after.elm", true);
    }

    public void testNoModuleDecl() {
        myFixture.configureByFiles("NoModuleDecl.elm", "LibraryA.elm");
        myFixture.launchAction(new ElmImportQuickFix("LibraryA.avril14th"));
        myFixture.checkResultByFile("NoModuleDecl_after.elm", true);
    }

    public void testExposing() {
        myFixture.configureByFiles("Exposing.elm", "LibraryA.elm");
        myFixture.launchAction(new ElmImportQuickFix("ageispolis"));
        myFixture.launchAction(new ElmImportQuickFix("avril14th"));
        myFixture.launchAction(new ElmImportQuickFix("actium"));
        myFixture.checkResultByFile("Exposing_after.elm", true);
    }

    public void testMixed() {
        myFixture.configureByFiles("Mixed.elm", "LibraryA.elm");
        myFixture.launchAction(new ElmImportQuickFix("LibraryA.ageispolis"));
        myFixture.launchAction(new ElmImportQuickFix("avril14th"));
        myFixture.launchAction(new ElmImportQuickFix("actium"));
        myFixture.checkResultByFile("Mixed_after.elm", true);
    }

    public void testAliased() {
        myFixture.configureByFiles("Aliased.elm", "LibraryA.elm");
        myFixture.launchAction(new ElmImportQuickFix("avril14th"));
        myFixture.checkResultByFile("Aliased_after.elm", true);
    }

    public void testHasModuleComment() {
        myFixture.configureByFiles("HasModuleComment.elm", "LibraryA.elm");
        myFixture.launchAction(new ElmImportQuickFix("LibraryA.avril14th"));
        myFixture.checkResultByFile("HasModuleComment_after.elm", true);
    }

    public void testSortedMulti() {
        myFixture.configureByFiles("SortedMulti.elm", "LibraryA.elm", "LibraryB.elm", "LibraryC.elm");
        myFixture.launchAction(new ElmImportQuickFix("LibraryB.beCool"));
        myFixture.launchAction(new ElmImportQuickFix("LibraryC.calamity"));
        myFixture.launchAction(new ElmImportQuickFix("LibraryA.avril14th"));
        myFixture.checkResultByFile("SortedMulti_after.elm", true);
    }

    public void testExposedUnionConstructor() {
        myFixture.configureByFiles("ExposedUnionConstructor.elm", "LibraryTypes.elm");
        myFixture.launchAction(new ElmImportQuickFix("NonModal"));
        myFixture.launchAction(new ElmImportQuickFix("Modal"));
        myFixture.launchAction(new ElmImportQuickFix("Overlay"));
        myFixture.checkResultByFile("ExposedUnionConstructor_after.elm", true);
    }

    public void testTypes() {
        myFixture.configureByFiles("Types.elm", "LibraryTypes.elm");
        myFixture.launchAction(new ElmImportQuickFix("LibraryTypes.Modal"));
        myFixture.launchAction(new ElmImportQuickFix("Behavior"));
        myFixture.launchAction(new ElmImportQuickFix("Overlay"));
        myFixture.launchAction(new ElmImportQuickFix("Modal"));
        myFixture.launchAction(new ElmImportQuickFix("NonModal"));
        myFixture.launchAction(new ElmImportQuickFix("Nonsense"));
        myFixture.launchAction(new ElmImportQuickFix("makeNonsense"));
        myFixture.launchAction(new ElmImportQuickFix("nonsenseToString"));
        myFixture.launchAction(new ElmImportQuickFix("MyModel"));
        myFixture.checkResultByFile("Types_after.elm", true);
    }
}
