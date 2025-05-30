// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.vuejs.web.symbols

import com.intellij.lang.javascript.psi.JSType
import com.intellij.model.Pointer
import com.intellij.webSymbols.PolySymbol
import com.intellij.webSymbols.PolySymbolOrigin
import com.intellij.webSymbols.PolySymbolQualifiedKind
import com.intellij.webSymbols.html.PolySymbolHtmlAttributeValue
import com.intellij.webSymbols.query.WebSymbolsQueryExecutor
import com.intellij.webSymbols.search.WebSymbolSearchTarget
import org.jetbrains.vuejs.model.VueComponent
import org.jetbrains.vuejs.model.VueContainer
import org.jetbrains.vuejs.model.VueEmitCall
import org.jetbrains.vuejs.model.VueModelOwner

private const val UPDATE_PREFIX = "update:"

class VueEmitCallSymbol(
  emitCall: VueEmitCall,
  owner: VueComponent,
  origin: PolySymbolOrigin,
) : VueNamedPolySymbol<VueEmitCall>(
  item = emitCall,
  origin = origin,
  owner = owner,
) {

  override val qualifiedKind: PolySymbolQualifiedKind
    get() = PolySymbol.JS_EVENTS

  override val type: JSType?
    get() = item.eventJSType

  override val priority: PolySymbol.Priority
    get() = PolySymbol.Priority.HIGHEST

  override val attributeValue: PolySymbolHtmlAttributeValue =
    PolySymbolHtmlAttributeValue.create(PolySymbolHtmlAttributeValue.Kind.EXPRESSION, PolySymbolHtmlAttributeValue.Type.OF_MATCH)

  override fun adjustNameForRefactoring(queryExecutor: WebSymbolsQueryExecutor, newName: String, occurence: String): String {
    if (item is VueModelOwner && occurence.startsWith(UPDATE_PREFIX) && !newName.startsWith(UPDATE_PREFIX)) {
      return "$UPDATE_PREFIX$newName"
    }
    return super.adjustNameForRefactoring(queryExecutor, newName, occurence)
  }

  override val searchTarget: WebSymbolSearchTarget
    get() = WebSymbolSearchTarget.create(this)

  override fun createPointer(): Pointer<VueEmitCallSymbol> =
    object : NamedSymbolPointer<VueEmitCall, VueEmitCallSymbol>(this) {

      override fun locateSymbol(owner: VueComponent): VueEmitCall? =
        (owner as? VueContainer)?.emits?.find { it.name == name }

      override fun createWrapper(owner: VueComponent, symbol: VueEmitCall): VueEmitCallSymbol =
        VueEmitCallSymbol(symbol, owner, origin)

    }
}