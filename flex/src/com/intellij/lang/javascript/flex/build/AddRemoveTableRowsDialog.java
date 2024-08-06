// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.lang.javascript.flex.build;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.TableUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public abstract class AddRemoveTableRowsDialog<T> extends DialogWrapper {

  protected final Project myProject;
  private final List<T> myList;

  private JPanel myMainPanel;
  protected JTable myTable;

  private boolean myEditAddedRow = false;

  public AddRemoveTableRowsDialog(final Project project, final String title, final List<T> list) {
    super(project);
    myProject = project;
    myList = list;
    setTitle(title);
  }

  @Override
  protected void init() {
    myMainPanel = new JPanel(new BorderLayout());

    initTable();
    initButtons();

    super.init();
  }

  protected void initTable() {
    myTable = new JBTable();
    myTable.setRowHeight(new JTextField("Fake").getPreferredSize().height + myTable.getRowMargin());
    myTable.setPreferredScrollableViewportSize(JBUI.size(400, 150));

    myTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); // otherwise model is not in sync with view

    myTable.setModel(createTableModel());
    myTable.setDefaultRenderer(Boolean.class, new NoBackgroundBooleanTableCellRenderer());

    final TableColumnModel columnModel = myTable.getColumnModel();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      columnModel.getColumn(i).setPreferredWidth(getPreferredColumnWidth(i));
    }
  }

  protected abstract TableModelBase createTableModel();

  protected int getPreferredColumnWidth(final int columnIndex) {
    return myTable.getColumnClass(columnIndex) == Boolean.class ? 50 : 200;
  }

  private void initButtons() {
    ToolbarDecorator d = ToolbarDecorator.createDecorator(myTable);
    d.setAddAction(new AnActionButtonRunnable() {
      @Override
      public void run(AnActionButton button) {
        if (addObject()) {
          ((AbstractTableModel)myTable.getModel()).fireTableDataChanged();
          if (myEditAddedRow) {
            myTable.editCellAt(myTable.getRowCount() - 1, 0);
          }
        }
      }
    });
    d.setRemoveAction(new AnActionButtonRunnable() {
      @Override
      public void run(AnActionButton anActionButton) {
        TableUtil.stopEditing(myTable);
        final int[] selectedRows = myTable.getSelectedRows();
        Arrays.sort(selectedRows);
        for (int i = selectedRows.length - 1; i >= 0; i--) {
          myList.remove(selectedRows[i]);
        }

        ((AbstractTableModel)myTable.getModel()).fireTableDataChanged();
      }
    });

    myMainPanel.add(d.createPanel(), BorderLayout.CENTER);
  }

  public void setEditAddedRow(final boolean editAddedRow) {
    myEditAddedRow = editAddedRow;
  }

  protected abstract boolean addObject();

  @Override
  protected JComponent createCenterPanel() {
    return myMainPanel;
  }

  @Override
  protected void doOKAction() {
    final TableCellEditor cellEditor = myTable.getCellEditor();
    if (cellEditor != null) {
      // apply currently edited value if any
      cellEditor.stopCellEditing();
    }
    super.doOKAction();
  }

  public List<T> getCurrentList() {
    return myList;
  }

  protected abstract class TableModelBase extends DefaultTableModel {

    @Override
    public abstract int getColumnCount();

    @Override
    public int getRowCount() {
      return myList.size();
    }

    @Override
    public abstract @Nullable String getColumnName(int column);

    @Override
    public abstract Class getColumnClass(int column);

    @Override
    public Object getValueAt(final int row, final int column) {
      return getValue(myList.get(row), column);
    }

    protected abstract Object getValue(final T t, final int column);

    @Override
    public void setValueAt(final Object aValue, final int row, final int column) {
      setValue(myList.get(row), column, aValue);
    }

    protected abstract void setValue(final T t, final int column, final Object aValue);
  }
}
