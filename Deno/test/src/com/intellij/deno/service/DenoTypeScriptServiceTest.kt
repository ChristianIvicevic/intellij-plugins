package com.intellij.deno.service

import com.intellij.deno.DenoSettings
import com.intellij.lang.javascript.BaseJSCompletionTestCase
import com.intellij.lang.javascript.modules.JSTempDirWithNodeInterpreterTest
import com.intellij.lang.javascript.typescript.service.TypeScriptServiceTestBase
import com.intellij.lang.typescript.compiler.languageService.TypeScriptLanguageServiceUtil
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.Disposer
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl

class DenoTypeScriptServiceTest : JSTempDirWithNodeInterpreterTest() {
  override fun setUp() {
    super.setUp()
    DenoSettings.getService(project).setUseDenoAndReload(true)
    (myFixture as CodeInsightTestFixtureImpl).canChangeDocumentDuringHighlighting(true)
    TypeScriptLanguageServiceUtil.setUseService(true)
    Disposer.register(testRootDisposable) {
      TypeScriptLanguageServiceUtil.setUseService(false)
    }
  }

  fun testSimpleDeno() {
    // one error comes from JSReferenceChecker.reportUnresolvedReference, another one - from Deno LSP server
    myFixture.configureByText("foo.ts", "console.log(Deno)\n" +
                                        "console.log(<error descr=\"Deno: Cannot find name 'Deno1'. Did you mean 'Deno'?\"><error descr=\"Unresolved variable or type Deno1\">Deno1</error></error>)")
    myFixture.checkLspHighlighting()
  }

  fun testDenoOpenCloseFile() {
    val file = myFixture.configureByText("bar.ts", "")
    myFixture.type("export class Hello {}\n" +
                   "<error descr=\"Deno: Cannot find name 'UnknownName'.\"><error descr=\"Unresolved variable or type UnknownName\">UnknownName</error></error>")
    FileEditorManager.getInstance(project).closeFile(file.virtualFile)

    myFixture.configureByText("foo.ts", "<weak_warning descr=\"Deno: All imports in import declaration are unused.\">import {" +
                                        "<warning descr=\"Deno: `Hello` is never used\nIf this is intentional, alias it with an underscore like `Hello as _Hello`\">Hello</warning>, " +
                                        "<error descr=\"Cannot resolve symbol 'Goodbye'\"><error descr=\"Deno: Module '\\\"./bar.ts\\\"' has no exported member 'Goodbye'.\">Goodbye</error></error>} from './bar.ts';</weak_warning>\n" +
                                        "<error descr=\"Deno: Cannot find name 'UnknownName'.\"><error descr=\"Unresolved variable or type UnknownName\">UnknownName</error></error>")
    myFixture.checkLspHighlighting()
    FileEditorManager.getInstance(project).closeFile(this.file.virtualFile)

    myFixture.openFileInEditor(file.virtualFile)
    myFixture.checkLspHighlighting()
  }

  fun testDenoSimpleRename() {
    val errorWithMarkup = "<error descr=\"Deno: Cannot find name 'UnknownName'.\"><error descr=\"Unresolved variable or type UnknownName\">UnknownName</error></error>"

    val fooFile = myFixture.addFileToProject("foo.ts", "export class Hello {}\n$errorWithMarkup")

    myFixture.configureByText("bar.ts",
                              "import { Hello } from './foo.ts'\nconst _baz = new Hello<caret>()\nconsole.log(_baz)\n$errorWithMarkup")
    myFixture.checkLspHighlighting()
    myFixture.checkResult("import { Hello } from './foo.ts'\nconst _baz = new Hello<caret>()\nconsole.log(_baz)\nUnknownName")

    myFixture.renameElementAtCaret("Hello1")
    WriteAction.run<Throwable> { myFixture.editor.document.setText(myFixture.editor.document.text.replace("UnknownName", errorWithMarkup)) }
    myFixture.checkLspHighlighting()
    myFixture.checkResult("import { Hello1 } from './foo.ts'\nconst _baz = new Hello<caret>1()\nconsole.log(_baz)\nUnknownName")

    myFixture.renameElementAtCaret("Hello12")
    WriteAction.run<Throwable> { myFixture.editor.document.setText(myFixture.editor.document.text.replace("UnknownName", errorWithMarkup)) }
    myFixture.checkLspHighlighting()
    myFixture.checkResult("import { Hello12 } from './foo.ts'\nconst _baz = new Hello<caret>12()\nconsole.log(_baz)\nUnknownName")

    myFixture.openFileInEditor(fooFile.virtualFile)
    myFixture.checkLspHighlighting()
    myFixture.checkResult("export class Hello12 {}\nUnknownName")
  }

  fun testDenoFileRename() {
    val errorWithMarkup = "<error descr=\"Deno: Cannot find name 'UnknownName'.\"><error descr=\"Unresolved variable or type UnknownName\">UnknownName</error></error>"

    myFixture.configureByText("foo.ts",
                              "import { Hello<caret> } from './subdir/bar.ts'\nconst _hi = new Hello()\nconsole.log(_hi)\n$errorWithMarkup")
    val bar = myFixture.addFileToProject("subdir/bar.ts", "export class Hello {}")
    myFixture.checkLspHighlighting()
    myFixture.checkResult("import { Hello<caret> } from './subdir/bar.ts'\nconst _hi = new Hello()\nconsole.log(_hi)\nUnknownName")

    myFixture.renameElementAtCaret("Hello1")
    myFixture.renameElement(file, "foo1.ts")
    myFixture.renameElement(bar, "bar1.ts")
    WriteAction.run<Throwable> { myFixture.editor.document.setText(myFixture.editor.document.text.replace("UnknownName", errorWithMarkup)) }
    myFixture.checkLspHighlighting()
    myFixture.checkResult("import { Hello<caret>1 } from './subdir/bar1.ts'\nconst _hi = new Hello1()\nconsole.log(_hi)\nUnknownName")

    myFixture.renameElementAtCaret("Hello2")
    FileDocumentManager.getInstance().saveAllDocuments()
    myFixture.moveFile("subdir/bar1.ts", "")
    FileDocumentManager.getInstance().saveAllDocuments()
    myFixture.moveFile("foo1.ts", "subdir")
    myFixture.renameElement(bar.containingDirectory, "subdir1")
    WriteAction.run<Throwable> { myFixture.editor.document.setText(myFixture.editor.document.text.replace("UnknownName", errorWithMarkup)) }
    myFixture.checkLspHighlighting()
    myFixture.checkResult("import { Hello<caret>2 } from '../bar1.ts'\nconst _hi = new Hello2()\nconsole.log(_hi)\nUnknownName")
  }

  fun testDenoModulePathCompletion() {
    myFixture.configureByText("main.ts", """
      import {join} from "https://deno.land/std@0.187.0/<caret>path/mod.ts";
      
      join("1", "2");
    """.trimIndent())
    myFixture.checkLspHighlighting()

    val lookupElements = myFixture.completeBasic()
    BaseJSCompletionTestCase.checkWeHaveInCompletion(lookupElements, "https://deno.land/std@0.187.0/path/mod.ts")
    myFixture.type("\t")
    myFixture.checkResult("""
      import {join} from "https://deno.land/std@0.187.0/path/mod.ts";
      
      join("1", "2");
    """.trimIndent())
    TypeScriptServiceTestBase.assertHasServiceItems(lookupElements, true)
  }
}