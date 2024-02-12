package org.intellij.prisma.ide.lsp

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.interpreter.wsl.WslNodeInterpreter
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem
import org.intellij.prisma.PrismaIcons
import org.intellij.prisma.lang.PrismaFileType

class PrismaLspServerSupportProvider : LspServerSupportProvider {
  override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
    if (file.fileType != PrismaFileType) return

    val node = NodeJsInterpreterManager.getInstance(project).interpreter
    if (node !is NodeJsLocalInterpreter && node !is WslNodeInterpreter) return

    serverStarter.ensureServerStarted(PrismaLspServerDescriptor(project))
  }

  override fun getLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?): LspServerWidgetItem =
    LspServerWidgetItem(lspServer, currentFile, PrismaIcons.PRISMA, settingsPageClass = null) // TODO add reasonable widget action
}