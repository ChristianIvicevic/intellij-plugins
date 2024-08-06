// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.lang.javascript.flex.flexunit;

import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.flex.model.bc.OutputType;
import com.intellij.flex.model.bc.TargetPlatform;
import com.intellij.lang.javascript.flex.FlexBundle;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfiguration;
import com.intellij.lang.javascript.flex.run.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FlexUnitRunConfigurationForm extends SettingsEditor<FlexUnitRunConfiguration> {
  private JPanel myMainPanel;

  private BCCombo myBCCombo;
  private WhatToTestForm myWhatToTestForm;

  private JCheckBox myShowLogCheckBox;
  private JComboBox myLogLevelCombo;
  private JLabel myLauncherParametersLabel;
  private TextFieldWithBrowseButton myLauncherParametersTextWithBrowse;
  private JCheckBox myRunTrustedCheckBox;
  private JBLabel myEmulatorAdlOptionsLabel;
  private RawCommandLineEditor myEmulatorAdlOptionsEditor;
  private JLabel myAppDescriptorForEmulatorLabel;
  private JComboBox myAppDescriptorForEmulatorCombo;

  private final Project myProject;

  private LauncherParameters myLauncherParameters;

  public FlexUnitRunConfigurationForm(final Project project) {
    myProject = project;

    myBCCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        myWhatToTestForm.updateOnBCChange(myBCCombo.getBC(), myBCCombo.getModule());
        myAppDescriptorForEmulatorCombo.repaint();
        updateControls();
      }
    });

    initLogLevelControls();
    initLaunchWithTextWithBrowse();

    FlashRunConfigurationForm.initAppDescriptorForEmulatorCombo(myAppDescriptorForEmulatorCombo,
                                                                () -> myBCCombo.getBC());
  }

  private void initLogLevelControls() {
    myShowLogCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (myShowLogCheckBox.isSelected()) {
          IdeFocusManager.getInstance(myProject).requestFocus(myLogLevelCombo, false);
        }
        updateControls();
      }
    });

    myLogLevelCombo.setModel(new EnumComboBoxModel<>(FlexUnitRunnerParameters.OutputLogLevel.class));
  }

  private void initLaunchWithTextWithBrowse() {
    myLauncherParametersTextWithBrowse.getTextField().setEditable(false);
    myLauncherParametersTextWithBrowse.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        final FlexLauncherDialog dialog = new FlexLauncherDialog(myProject, myLauncherParameters);
        if (dialog.showAndGet()) {
          myLauncherParameters = dialog.getLauncherParameters();
          updateControls();
        }
      }
    });
  }

  private void updateControls() {
    if (myShowLogCheckBox.isSelected()) {
      myLogLevelCombo.setEnabled(true);
      if (myLogLevelCombo.getSelectedItem() == null) {
        myLogLevelCombo.setSelectedItem(FlexUnitRunnerParameters.OutputLogLevel.values()[0]);
      }
    }
    else {
      myLogLevelCombo.setEnabled(false);
    }

    final FlexBuildConfiguration bc = myBCCombo.getBC();
    final boolean webPlatform = bc != null && bc.getTargetPlatform() == TargetPlatform.Web;
    final boolean mobilePlatform = bc != null && bc.getTargetPlatform() == TargetPlatform.Mobile;
    final boolean app = bc != null && bc.getOutputType() == OutputType.Application;

    myLauncherParametersLabel.setVisible(webPlatform);
    myLauncherParametersTextWithBrowse.setVisible(webPlatform);
    myRunTrustedCheckBox.setVisible(webPlatform);

    myEmulatorAdlOptionsLabel.setVisible(mobilePlatform);
    myEmulatorAdlOptionsEditor.setVisible(mobilePlatform);
    myAppDescriptorForEmulatorLabel.setVisible(app && mobilePlatform);
    myAppDescriptorForEmulatorCombo.setVisible(app && mobilePlatform);

    if (webPlatform) {
      myLauncherParametersTextWithBrowse.getTextField().setText(myLauncherParameters.getPresentableText());
    }
  }

  @Override
  protected @NotNull JComponent createEditor() {
    return myMainPanel;
  }

  @Override
  protected void resetEditorFrom(final @NotNull FlexUnitRunConfiguration config) {
    final FlexUnitRunnerParameters params = config.getRunnerParameters();
    myLauncherParameters = params.getLauncherParameters().clone(); // must be before myBCsCombo.setModel()

    myBCCombo.resetFrom(params);
    myWhatToTestForm.resetFrom(myBCCombo.getModule(), myBCCombo.getBC(), params);

    myShowLogCheckBox.setSelected(params.getOutputLogLevel() != null);
    myLogLevelCombo.setEnabled(params.getOutputLogLevel() != null);
    myLogLevelCombo.setSelectedItem(params.getOutputLogLevel());

    myRunTrustedCheckBox.setSelected(params.isTrusted());

    myEmulatorAdlOptionsEditor.setText(params.getEmulatorAdlOptions());
    myAppDescriptorForEmulatorCombo.setSelectedItem(params.getAppDescriptorForEmulator());
  }

  @Override
  protected void applyEditorTo(final @NotNull FlexUnitRunConfiguration config) throws ConfigurationException {
    final FlexUnitRunnerParameters params = config.getRunnerParameters();

    myBCCombo.applyTo(params);
    myWhatToTestForm.applyTo(params);

    final FlexUnitRunnerParameters.OutputLogLevel logLevel = myShowLogCheckBox.isSelected()
                                                             ? (FlexUnitRunnerParameters.OutputLogLevel)myLogLevelCombo.getSelectedItem()
                                                             : null;
    params.setOutputLogLevel(logLevel);
    params.setLauncherParameters(myLauncherParameters);
    params.setTrusted(myRunTrustedCheckBox.isSelected());

    params.setEmulatorAdlOptions(myEmulatorAdlOptionsEditor.getText().trim());
    params.setAppDescriptorForEmulator((FlashRunnerParameters.AppDescriptorForEmulator)myAppDescriptorForEmulatorCombo.getSelectedItem());
  }

  @Override
  protected void disposeEditor() {
    myBCCombo.dispose();
    myWhatToTestForm.dispose();
  }

  private void createUIComponents() {
    myBCCombo = new BCCombo(myProject);
    myWhatToTestForm = new WhatToTestForm(myProject,
                                          () -> {
                                            final Module module = myBCCombo.getModule();
                                            if (module != null) return module;
                                            throw new RuntimeConfigurationError(FlexBundle.message("bc.not.specified"));
                                          },
                                          () -> {
                                            final FlexBuildConfiguration bc = myBCCombo.getBC();
                                            if (bc == null) throw new RuntimeConfigurationError(FlexBundle.message("bc.not.specified"));
                                            final FlexUnitSupport support = FlexUnitSupport.getSupport(bc, myBCCombo.getModule());
                                            if (support != null) return support;

                                            throw new RuntimeConfigurationError(
                                              FlexBundle.message("flexunit.not.found.for.bc", bc.getName()));
                                          }
    );
  }
}
