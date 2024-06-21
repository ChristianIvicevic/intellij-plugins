// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.intellij.prisma.lang.psi.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.intellij.prisma.lang.psi.PrismaNamedElement

abstract class PrismaNamedStubElementType<S : PrismaNamedStub<P>, P : PrismaNamedElement>(
  debugName: String,
  private val psiFactory: (S, IStubElementType<out StubElement<*>, *>) -> P,
  private val stubFactory: (StubElement<out PsiElement>?, IStubElementType<out StubElement<*>, *>, String?) -> S,
) : PrismaStubElementType<S, P>(debugName) {
  override fun serialize(stub: S, dataStream: StubOutputStream) {
    super.serialize(stub, dataStream)
    dataStream.writeName(stub.name)
  }

  override fun createPsi(stub: S): P {
    return psiFactory(stub, this)
  }

  override fun createStub(psi: P, parentStub: StubElement<out PsiElement>?): S {
    return stubFactory(parentStub, this, psi.name)
  }

  override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): S {
    val name = dataStream.readNameString()
    return stubFactory(parentStub, this, name)
  }
}