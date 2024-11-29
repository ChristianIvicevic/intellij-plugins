package com.jetbrains.lang.dart.sdk;

import com.intellij.openapi.roots.libraries.LibraryProperties;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DartPackagesLibraryProperties extends LibraryProperties<DartPackagesLibraryProperties> {

  private @NotNull Map<String, List<String>> myPackageNameToDirsMap;

  public DartPackagesLibraryProperties() {
    myPackageNameToDirsMap = new TreeMap<>();
  }

  @NotNull
  @MapAnnotation(surroundWithTag = false)
  public Map<String, List<String>> getPackageNameToDirsMap() {
    return myPackageNameToDirsMap;
  }

  public void setPackageNameToDirsMap(@NotNull final Map<String, List<String>> packageNameToDirsMap) {
    myPackageNameToDirsMap = packageNameToDirsMap;
  }

  @Override
  @Nullable
  public DartPackagesLibraryProperties getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull final DartPackagesLibraryProperties state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof DartPackagesLibraryProperties &&
           myPackageNameToDirsMap.equals(((DartPackagesLibraryProperties)obj).getPackageNameToDirsMap());
  }

  @Override
  public int hashCode() {
    return myPackageNameToDirsMap.hashCode();
  }
}
