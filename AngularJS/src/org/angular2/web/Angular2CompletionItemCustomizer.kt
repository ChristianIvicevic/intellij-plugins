// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.angular2.web

import com.intellij.javascript.webSymbols.decorateWithJsType
import com.intellij.javascript.webSymbols.decorateWithSymbolType
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.psi.xml.XmlTag
import com.intellij.webSymbols.FrameworkId
import com.intellij.webSymbols.SymbolKind
import com.intellij.webSymbols.SymbolNamespace
import com.intellij.webSymbols.WebSymbol
import com.intellij.webSymbols.completion.WebSymbolCodeCompletionItem
import com.intellij.webSymbols.completion.WebSymbolCodeCompletionItemCustomizer
import org.angular2.Angular2Framework
import org.angular2.lang.types.BindingsTypeResolver
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_ATTRIBUTES
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_ATTRIBUTE_SELECTORS
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_ELEMENT_SELECTORS
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_EXPORTS_AS
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_INPUTS
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_IN_OUTS
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_ONE_TIME_BINDINGS
import org.angular2.web.Angular2WebSymbolsQueryConfigurator.Companion.KIND_NG_DIRECTIVE_OUTPUTS
import org.angular2.web.scopes.OneTimeBindingsScope


private val typedKinds = setOf(WebSymbol.KIND_JS_EVENTS,
                               WebSymbol.KIND_JS_PROPERTIES,
                               KIND_NG_DIRECTIVE_ATTRIBUTES,
                               KIND_NG_DIRECTIVE_INPUTS,
                               KIND_NG_DIRECTIVE_OUTPUTS,
                               KIND_NG_DIRECTIVE_IN_OUTS)

private val selectorKinds = setOf(KIND_NG_DIRECTIVE_ELEMENT_SELECTORS,
                                  KIND_NG_DIRECTIVE_ATTRIBUTE_SELECTORS)


class Angular2CompletionItemCustomizer : WebSymbolCodeCompletionItemCustomizer {

  override fun customize(item: WebSymbolCodeCompletionItem,
                         framework: FrameworkId?,
                         namespace: SymbolNamespace,
                         kind: SymbolKind,
                         location: PsiElement): WebSymbolCodeCompletionItem =
    if (namespace == WebSymbol.NAMESPACE_HTML && framework == Angular2Framework.ID)
      when (kind) {
        WebSymbol.KIND_HTML_ATTRIBUTES, WebSymbol.KIND_HTML_ATTRIBUTE_VALUES ->
          item.symbol
            ?.let { symbol ->
              val symbolKind = symbol.kind
              when {
                typedKinds.contains(symbolKind) -> item.decorateWithSymbolType(symbol)
                selectorKinds.contains(symbolKind) -> item.withPriority(WebSymbol.Priority.HIGH)

                // One time bindings and selectors require special handling
                // to not override standard attributes and elements
                symbolKind == KIND_NG_DIRECTIVE_ONE_TIME_BINDINGS ->
                  item
                    .decorateWithSymbolType(symbol)
                    .withPriority(
                      symbol.properties[OneTimeBindingsScope.PROP_DELEGATE_PRIORITY] as WebSymbol.Priority?
                      ?: WebSymbol.Priority.HIGH
                    )

                symbolKind == KIND_NG_DIRECTIVE_EXPORTS_AS ->
                  item.decorateWithJsType(
                    location.parentOfType<XmlTag>()?.let { BindingsTypeResolver.get(it).resolveDirectiveExportAsType(item.name) })
                else -> item
              }
            }
          ?: item
        else -> item
      }
    else if (namespace == WebSymbol.NAMESPACE_JS
             && framework == Angular2Framework.ID
             && typedKinds.contains(item.symbol?.kind))
      item.decorateWithSymbolType(item.symbol)
    else
      item

}