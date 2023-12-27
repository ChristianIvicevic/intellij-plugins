package com.intellij.flex.model.bc.impl;

import com.intellij.flex.model.bc.JpsFlexDependencyEntry;
import com.intellij.flex.model.bc.LinkageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.ex.JpsCompositeElementBase;

abstract class JpsFlexDependencyEntryBase<Self extends JpsFlexDependencyEntryBase<Self>> extends JpsCompositeElementBase<Self>
  implements JpsFlexDependencyEntry {

  LinkageType myLinkageType;

  protected JpsFlexDependencyEntryBase(final LinkageType linkageType) {
    myLinkageType = linkageType;
  }

  protected JpsFlexDependencyEntryBase(final JpsFlexDependencyEntryBase<Self> original) {
    super(original);
    myLinkageType = original.myLinkageType;
  }

// ------------------------------------

  @Override
  @NotNull
  public LinkageType getLinkageType() {
    return myLinkageType;
  }

  @Override
  public void setLinkageType(final LinkageType linkageType) {
    myLinkageType = linkageType;
  }
}
