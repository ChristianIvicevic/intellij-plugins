// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.angular2.codeInsight.inspections

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInsight.daemon.impl.analysis.HtmlUnknownTargetInspection
import com.intellij.htmltools.codeInspection.htmlInspections.HtmlFormInputWithoutLabelInspection
import com.intellij.lang.javascript.JSTestUtils
import com.intellij.lang.javascript.inspections.*
import com.intellij.lang.typescript.inspections.TypeScriptUnresolvedReferenceInspection
import com.intellij.webSymbols.moveToOffsetBySignature
import org.angular2.Angular2CodeInsightFixtureTestCase
import org.angularjs.AngularTestUtil

/**
 * @see Angular2DecoratorInspectionsTest
 * @see Angular2TemplateInspectionsTest
 */
class Angular2TsInspectionsTest : Angular2CodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String {
    return AngularTestUtil.getBaseTestDataPath() + "inspections/ts"
  }

  fun testUnusedSymbol() {
    myFixture.enableInspections(JSUnusedGlobalSymbolsInspection::class.java,
                                JSUnusedLocalSymbolsInspection::class.java)
    myFixture.configureByFiles("unused.ts", "unused.html", "package.json")
    myFixture.checkHighlighting()
  }

  fun testUnusedSetter() {
    myFixture.enableInspections(JSUnusedGlobalSymbolsInspection::class.java,
                                JSUnusedLocalSymbolsInspection::class.java)
    myFixture.configureByFiles("unusedSetter.ts", "unusedSetter.html", "package.json")
    myFixture.checkHighlighting()
  }

  fun testMethodCanBeStatic() {
    val canBeStaticInspection = JSMethodCanBeStaticInspection()
    JSTestUtils.setInspectionHighlightLevel(project, canBeStaticInspection, HighlightDisplayLevel.WARNING, testRootDisposable)
    myFixture.enableInspections(canBeStaticInspection)
    myFixture.configureByFiles("methodCanBeStatic.ts", "methodCanBeStatic.html", "package.json")
    myFixture.checkHighlighting()
  }

  fun testUnterminated() {
    myFixture.enableInspections(UnterminatedStatementJSInspection::class.java)
    myFixture.configureByFiles("unterminated.ts", "package.json")
    myFixture.checkHighlighting()
  }

  fun testUnusedReference() {
    myFixture.enableInspections(JSUnusedGlobalSymbolsInspection::class.java,
                                JSUnusedLocalSymbolsInspection::class.java)
    myFixture.configureByFiles("unusedReference.html", "unusedReference.ts", "package.json")
    myFixture.checkHighlighting()
    for (attrToRemove in mutableListOf("notUsedRef", "anotherNotUsedRef", "notUsedRefWithAttr", "anotherNotUsedRefWithAttr")) {
      myFixture.moveToOffsetBySignature("<caret>$attrToRemove")
      myFixture.launchAction(
        try {
          myFixture.findSingleIntention("Remove unused variable '$attrToRemove'")
        }
        catch (e: AssertionError) {
          myFixture.findSingleIntention("Remove unused constant '$attrToRemove'")
        })
    }
    myFixture.checkResultByFile("unusedReference.after.html")
  }

  fun testId() {
    myFixture.enableInspections(JSUnusedLocalSymbolsInspection::class.java,
                                JSUnusedGlobalSymbolsInspection::class.java)
    myFixture.configureByFiles("object.ts", "package.json")
    myFixture.checkHighlighting()
  }

  fun testPipeAndArgResolution() {
    myFixture.enableInspections(TypeScriptUnresolvedReferenceInspection::class.java)
    myFixture.configureByFiles("pipeAndArgResolution.html", "lowercase_pipe.ts", "package.json")
    myFixture.checkHighlighting()
  }

  fun testHtmlTargetWithInterpolation() {
    myFixture.enableInspections(HtmlUnknownTargetInspection::class.java)
    myFixture.configureByFiles("htmlTargetWithInterpolation.html", "package.json")
    myFixture.checkHighlighting()
  }

  fun testDuplicateDeclarationOff() {
    myFixture.enableInspections(JSDuplicatedDeclarationInspection())
    myFixture.configureByFiles("duplicateDeclarationOff.html", "duplicateDeclarationOff.ts", "package.json")
    myFixture.checkHighlighting()
  }

  fun testDuplicateDeclarationOffTemplate() {
    myFixture.enableInspections(JSDuplicatedDeclarationInspection())
    myFixture.configureByFiles("duplicateDeclarationOffLocalTemplate.ts", "package.json")
    myFixture.checkHighlighting()
  }

  fun testEmptyVarDefinition() {
    myFixture.enableInspections(JSUnusedLocalSymbolsInspection())
    myFixture.configureByFiles("package.json")
    myFixture.configureByText("template.html", "<ng-template ngFor let- ></ng-template>")
    myFixture.checkHighlighting()
  }

  fun testMissingLabelSuppressed() {
    myFixture.enableInspections(HtmlFormInputWithoutLabelInspection())
    myFixture.configureByFiles("missingLabelSuppressed.html", "missingLabelSuppressed.ts", "package.json")
    myFixture.checkHighlighting()
  }
}
