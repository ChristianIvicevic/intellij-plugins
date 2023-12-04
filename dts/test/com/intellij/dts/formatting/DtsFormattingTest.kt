package com.intellij.dts.formatting

import com.intellij.dts.lang.DtsFileType
import com.intellij.dts.lang.DtsLanguage
import com.intellij.dts.settings.DtsCodeStyleSettings
import com.intellij.psi.formatter.FormatterTestCase

abstract class DtsFormattingTest : FormatterTestCase() {
  override fun getTestDataPath() = "testData/formatting"

  override fun getFileExtension() = "dtsi"

  override fun getTestName(lowercaseFirstLetter: Boolean): String = super.getTestName(false)

  protected fun doFormattingTest(
    keepLineBreaks: Boolean = false,
    alignPropertyAssignment: Boolean = false,
    alignPropertyValues: Boolean = false,
    maxBlankLinesBetweenProperties: Int = 1,
    minBlankLinesBetweenProperties: Int = 0,
    maxBlankLinesBetweenNodes: Int = 1,
    minBlankLinesBetweenNodes: Int = 1
  ) {
    val indent = settings.getIndentOptions(DtsFileType)
    indent.USE_TAB_CHARACTER = false

    val common = settings.getCommonSettings(DtsLanguage)
    common.KEEP_LINE_BREAKS = keepLineBreaks

    val custom = settings.getCustomSettings(DtsCodeStyleSettings::class.java)
    custom.ALIGN_PROPERTY_ASSIGNMENT = alignPropertyAssignment
    custom.ALIGN_PROPERTY_VALUES = alignPropertyValues
    custom.MAX_BLANK_LINES_BETWEEN_PROPERTIES = maxBlankLinesBetweenProperties
    custom.MIN_BLANK_LINES_BETWEEN_PROPERTIES = minBlankLinesBetweenProperties
    custom.MAX_BLANK_LINES_BETWEEN_NODES = maxBlankLinesBetweenNodes
    custom.MIN_BLANK_LINES_BETWEEN_NODES = minBlankLinesBetweenNodes

    doTest()
  }
}