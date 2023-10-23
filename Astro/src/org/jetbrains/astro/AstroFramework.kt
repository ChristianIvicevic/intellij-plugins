// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.astro

import com.intellij.javascript.web.WebFramework
import com.intellij.javascript.web.html.WebFrameworkHtmlFileType
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class AstroFramework : WebFramework() {

  override fun isOwnTemplateLanguage(language: Language): Boolean = false

  override fun getFileType(kind: SourceFileKind, context: VirtualFile, project: Project): WebFrameworkHtmlFileType? = null

  override val displayName: String = "Astro"

  override val icon: Icon
    get() = AstroIcons.Astro

  companion object {
    val instance: WebFramework
      get() = get(ID)
    const val ID = "astro"
  }
}