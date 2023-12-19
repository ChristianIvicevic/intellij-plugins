package com.intellij.javascript.karma.coverage;

import com.intellij.coverage.CoverageRunner;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;

public class KarmaCoverageEnabledConfiguration extends CoverageEnabledConfiguration {
  public KarmaCoverageEnabledConfiguration(RunConfigurationBase configuration) {
    super(configuration);
    setCoverageRunner(CoverageRunner.getInstance(KarmaCoverageRunner.class));
  }
}
