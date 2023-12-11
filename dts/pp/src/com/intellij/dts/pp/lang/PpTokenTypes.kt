package com.intellij.dts.pp.lang

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

interface PpTokenTypes {
  val defineStatement: IElementType
  val define: IElementType
  val defineValue: IElementType

  val endifStatement: IElementType
  val endif: IElementType

  val ifdefStatement: IElementType
  val ifdef: IElementType

  val ifndefStatement: IElementType
  val ifndef: IElementType

  val includeStatement: IElementType
  val include: IElementType
  val includePath: IElementType

  val undefStatement: IElementType
  val undef: IElementType

  val symbol: IElementType

  val statementEnd: IElementType
  val statementMarker: IElementType

  fun createScopeSet(): TokenSet

  fun createDirectivesSet(): TokenSet = TokenSet.create(
    include,
    define,
    endif,
    ifdef,
    ifndef,
    undef,
  )

  fun createStatementSet(): TokenSet = TokenSet.create(
    includeStatement,
    defineStatement,
    endifStatement,
    ifdefStatement,
    ifndefStatement,
    undefStatement,
  )
}