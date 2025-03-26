// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.terraform.template.editor

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.ProcessingContext
import com.intellij.terraform.template.model.TftplVariable
import com.intellij.terraform.template.model.collectAvailableVariables

internal class InjectedHilTemplateCompletionContributor : CompletionContributor() {
  init {
    extend(CompletionType.BASIC, createHilVariablePattern(::isInjectedHil), InjectedHilTemplateVariableCompletionProvider())
  }
}

private class InjectedHilTemplateVariableCompletionProvider : CompletionProvider<CompletionParameters>() {
  override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
    val effectivePosition = translateToTemplateLanguageElement(parameters.position) ?: return
    collectAvailableVariables(effectivePosition).forEach { variable ->
      ProgressManager.checkCanceled()
      result.addElement(createVariableLookup(variable))
    }
  }

  private fun createVariableLookup(variable: TftplVariable): LookupElement {
    return createVariableLookupSkeleton(variable)
      .let { lookup -> PrioritizedLookupElement.withPriority(lookup, HIGH_COMPLETION_PRIORITY) }
  }
}