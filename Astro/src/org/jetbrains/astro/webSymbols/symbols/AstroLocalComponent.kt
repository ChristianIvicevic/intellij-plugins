// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.astro.webSymbols.symbols

import com.intellij.model.Pointer
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.createSmartPointer
import com.intellij.util.containers.Stack
import com.intellij.webSymbols.*
import com.intellij.webSymbols.WebSymbol.Companion.KIND_HTML_ATTRIBUTES
import com.intellij.webSymbols.WebSymbol.Companion.NAMESPACE_HTML
import com.intellij.webSymbols.query.WebSymbolsListSymbolsQueryParams
import com.intellij.webSymbols.query.WebSymbolsNameMatchQueryParams
import org.jetbrains.astro.webSymbols.AstroProximity
import org.jetbrains.astro.webSymbols.AstroQueryConfigurator

class AstroLocalComponent(override val name: String,
                          override val source: PsiElement,
                          override val priority: WebSymbol.Priority = WebSymbol.Priority.HIGH) : PsiSourcedWebSymbol {

  override fun getMatchingSymbols(namespace: SymbolNamespace,
                                  kind: SymbolKind,
                                  name: String,
                                  params: WebSymbolsNameMatchQueryParams,
                                  scope: Stack<WebSymbolsScope>): List<WebSymbol> =
    if (namespace == NAMESPACE_HTML && kind == KIND_HTML_ATTRIBUTES && name.contains(":"))
      emptyList()
    else
      super.getMatchingSymbols(namespace, kind, name, params, scope)

  override fun getSymbols(namespace: SymbolNamespace,
                          kind: SymbolKind,
                          params: WebSymbolsListSymbolsQueryParams,
                          scope: Stack<WebSymbolsScope>): List<WebSymbolsScope> =
    if (namespace == NAMESPACE_HTML && kind == KIND_HTML_ATTRIBUTES && !params.expandPatterns)
      listOf(AstroComponentWildcardAttribute)
    else
      emptyList()

  override val origin: WebSymbolOrigin
    get() = AstroProjectSymbolOrigin

  override val namespace: SymbolNamespace
    get() = NAMESPACE_HTML

  override val kind: SymbolKind
    get() = AstroQueryConfigurator.KIND_ASTRO_COMPONENT

  override val properties: Map<String, Any>
    get() = mapOf(Pair(AstroQueryConfigurator.PROP_ASTRO_PROXIMITY, AstroProximity.LOCAL))

  override fun createPointer(): Pointer<out PsiSourcedWebSymbol> {
    val name = name
    val sourcePtr = source.createSmartPointer()
    return Pointer {
      sourcePtr.dereference()?.let { AstroLocalComponent(name, it) }
    }
  }

}