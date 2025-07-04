// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.angular2.web

import com.intellij.model.Pointer
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.polySymbols.utils.PolySymbolDelegate
import com.intellij.polySymbols.PolySymbolOrigin

abstract class Angular2SymbolDelegate<T : Angular2Symbol>(delegate: T) : PolySymbolDelegate<T>(delegate), Angular2Symbol {

  abstract override fun createPointer(): Pointer<out Angular2SymbolDelegate<T>>

  override val project: Project
    get() = delegate.project

  override val origin: PolySymbolOrigin
    get() = delegate.origin

  override fun getNavigationTargets(project: Project): Collection<NavigationTarget> =
    super<PolySymbolDelegate>.getNavigationTargets(project)

}