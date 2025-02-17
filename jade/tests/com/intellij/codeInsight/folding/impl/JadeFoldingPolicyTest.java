// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInsight.folding.impl;

public class JadeFoldingPolicyTest extends AbstractFoldingPolicyTest {
  public void testNameWithSpecialCharacter() {
    doTest("""
             html
                 #container
                     p.
                       text""",
           "jade");
  }
}
