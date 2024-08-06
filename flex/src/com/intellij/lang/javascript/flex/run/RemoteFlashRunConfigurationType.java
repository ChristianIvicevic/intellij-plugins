// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.lang.javascript.flex.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.lang.javascript.flex.FlexModuleType;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import icons.FlexIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class RemoteFlashRunConfigurationType implements ConfigurationType, DumbAware {

  public static final String TYPE = "RemoteFlashRunConfigurationType";
  public static final String DISPLAY_NAME = "Flash Remote Debug";

  private final ConfigurationFactory myFactory;

  public RemoteFlashRunConfigurationType() {
    myFactory = new ConfigurationFactory(this) {
      @Override
      public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new RemoteFlashRunConfiguration(project, this, "");
      }

      @Override
      public boolean isApplicable(@NotNull Project project) {
        return ModuleUtil.hasModulesOfType(project, FlexModuleType.getInstance());
      }

      @Override
      public @NotNull String getId() {
        return "Flash Remote Debug";
      }
    };
  }

  @Override
  public @NotNull String getDisplayName() {
    return DISPLAY_NAME;
  }

  @Override
  public String getConfigurationTypeDescription() {
    return "Remote Flash debug configuration";
  }

  @Override
  public Icon getIcon() {
    return FlexIcons.Flash_remote_debug;
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{myFactory};
  }

  @Override
  public String getHelpTopic() {
    return "reference.dialogs.rundebug.RemoteFlashRunConfigurationType";
  }

  @Override
  public @NotNull String getId() {
    return TYPE;
  }

  public static RemoteFlashRunConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(RemoteFlashRunConfigurationType.class);
  }

  public static ConfigurationFactory getFactory() {
    return getInstance().getConfigurationFactories()[0];
  }
}
