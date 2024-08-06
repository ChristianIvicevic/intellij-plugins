// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.lang.javascript.flex.build;

import org.jetbrains.annotations.Nullable;

public class FlexCompilerException extends Exception {

  private final @Nullable String myUrl;
  private final int myLine;
  private final int myColumn;

  public FlexCompilerException(final String message) {
    super(message);
    myUrl = null;
    myLine = -1;
    myColumn = -1;
  }

  public FlexCompilerException(final String message, final String url, final int line, final int column) {
    super(message);
    myUrl = url;
    myLine = line;
    myColumn = column;
  }

  public FlexCompilerException(final String message, final String url) {
    super(message);
    myUrl = url;
    myLine = -1;
    myColumn = -1;
  }

  public @Nullable String getUrl() {
    return myUrl;
  }

  public int getLine() {
    return myLine;
  }

  public int getColumn() {
    return myColumn;
  }
}
