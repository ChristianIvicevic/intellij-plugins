package com.intellij.dts.lang.stubs.impl

import com.intellij.dts.lang.psi.DtsSubNode
import com.intellij.dts.lang.psi.impl.DtsSubNodeImpl
import com.intellij.dts.lang.stubs.DTS_NODE_LABEL_INDEX
import com.intellij.dts.lang.stubs.DtsStubElementType
import com.intellij.dts.lang.stubs.readUTFList
import com.intellij.dts.lang.stubs.writeUTFList
import com.intellij.psi.stubs.*

class DtsSubNodeStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>,
    val labels: List<String>,
) : StubBase<DtsSubNode>(parent, elementType) {
    class Type(debugName: String) : DtsStubElementType<DtsSubNodeStub, DtsSubNode>(debugName) {
        override fun createPsi(stub: DtsSubNodeStub): DtsSubNode = DtsSubNodeImpl(stub, this)

        override fun createStub(psi: DtsSubNode, parentStub: StubElement<*>?): DtsSubNodeStub {
            return DtsSubNodeStub(parentStub, this, psi.dtsLabels)
        }

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): DtsSubNodeStub {
            return DtsSubNodeStub(parentStub, this, dataStream.readUTFList())
        }

        override fun serialize(stub: DtsSubNodeStub, dataStream: StubOutputStream) {
            dataStream.writeUTFList(stub.labels)
        }

        override fun indexStub(stub: DtsSubNodeStub, sink: IndexSink) {
            for (label in stub.labels) {
                sink.occurrence(DTS_NODE_LABEL_INDEX, label)
            }
        }
    }
}