package com.intellij.dts.lang.psi.mixin

import com.intellij.dts.DtsIcons
import com.intellij.dts.lang.DtsAffiliation
import com.intellij.dts.lang.psi.*
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

abstract class DtsPropertyMixin(node: ASTNode) : ASTWrapperPsiElement(node), DtsProperty {
  override val dtsAffiliation: DtsAffiliation
    get() = DtsAffiliation.NODE

  override val dtsStatementKind: DtsStatementKind
    get() = DtsStatementKind.PROPERTY

  override val dtsIsComplete: Boolean
    get() = !PsiTreeUtil.hasErrorElements(this)

  override val dtsNameElement: PsiElement
    get() = findNotNullChildByType<PsiElement>(DtsTypes.NAME)

  override val dtsName: String
    get() = dtsNameElement.text

  override val dtsValues: List<DtsValue>
    get() {
      val content = findChildByClass(DtsPropertyContent::class.java) ?: return emptyList()
      return PsiTreeUtil.getChildrenOfTypeAsList(content, DtsValue::class.java)
    }

  override fun getTextOffset(): Int = dtsNameElement.textOffset

  override fun getIcon(flags: Int): Icon = DtsIcons.Property
}