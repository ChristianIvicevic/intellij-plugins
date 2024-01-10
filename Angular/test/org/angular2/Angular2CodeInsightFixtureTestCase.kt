// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.angular2

import com.intellij.testFramework.fixtures.BasePlatformTestCase

@Deprecated("Use Angular2TestCase")
abstract class Angular2CodeInsightFixtureTestCase : BasePlatformTestCase() {

  @Throws(Exception::class)
  override fun setUp() {
    super.setUp()
    Angular2TestUtil.enableAstLoadingFilter(this)
  }
}
