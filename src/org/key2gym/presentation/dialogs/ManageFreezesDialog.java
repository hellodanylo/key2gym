/*
 * Copyright 2012 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.key2gym.presentation.dialogs;

import org.key2gym.business.FreezesService;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.dto.FreezeDTO;
import org.key2gym.presentation.util.FreezesTableModel;
import org.key2gym.presentation.util.FreezesTableModel.Column;
import org.key2gym.presentation.util.UserExceptionHandler;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageFreezesDialog extends AbstractDialog {

    /**
     * Creates new ManageFreezesDialog
     */
    public ManageFreezesDialog(JFrame parent) {
        super(parent, true);
        freezesService = FreezesService.getInstance();

        buildDialog();
    }

    private void buildDialog() {
        setLayout(new FormLayout("4dlu, d:g, 4dlu, d, 4dlu", "4dlu, f:d:g, 4dlu, d, 4dlu, d, 4dlu"));
        
        add(createFreezesScrollPane(), CC.xy(2, 2));
        add(createNoteScrollPane(), CC.xy(2, 4));
        add(createControlPanel(), CC.xywh(4, 2, 1, 3));
        add(createButtonsPanel(), CC.xywh(2, 6, 3, 1));
        
        getRootPane().setDefaultButton(okButton);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.Freezes")); // NOI18N
        pack();
        setMinimumSize(getPreferredSize());
    }
    
    /**
     * Creates a freezes scroll pane.
     * 
     * @return the created scroll pane
     */
    private JScrollPane createFreezesScrollPane() {
        JScrollPane freezesScrollPane = new JScrollPane();

        Column[] columns = new Column[]{
            Column.ADMINISTRATOR_FULL_NAME,
            Column.CLIENT_FULL_NAME,
            Column.DATE_ISSUED,
            Column.DAYS,
            Column.DATE_EXPIRED,
            Column.NOTE
        };

        freezesTableModel = new FreezesTableModel(columns);
        freezesTable = new JTable();
        freezesTable.setModel(freezesTableModel);
        freezesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        freezesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (freezesTable.getSelectedRowCount() == 1) {
                    removeButton.setEnabled(true);
                    noteTextArea.setText(freezesTableModel.getFreezeAt(freezesTable.getSelectedRow()).getNote());
                } else {
                    removeButton.setEnabled(false);
                    noteTextArea.setText("");
                }
            }
        });
        freezesScrollPane.setViewportView(freezesTable);

        return freezesScrollPane;
    }
    
    /**
     * Creates a note scroll pane.
     * 
     * @return the scroll pane
     */
    private JScrollPane createNoteScrollPane() {
        JScrollPane noteScrollPane = new JScrollPane();
        noteScrollPane.setBorder(BorderFactory.createTitledBorder(getString("Text.Note"))); // NOI18N

        noteTextArea = new JTextArea();
        noteTextArea.setColumns(20);
        noteTextArea.setLineWrap(true);
        noteTextArea.setRows(5);
        noteTextArea.setEnabled(false);
        noteScrollPane.setViewportView(noteTextArea);

        return noteScrollPane;
    }
    
    /**
     * Creates a control panel.
     * 
     * @return the created panel
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FormLayout("d", "t:d:g, t:d:g"));

        periodsComboBox = new JComboBox();
        String[] periods = new String[]{
            getString("Text.Last7Days"),
            getString("Text.LastMonth"),
            getString("Text.Last3Months"),
            getString("Text.All")
        };
        periodsComboBox.setModel(new DefaultComboBoxModel(periods));
        periodsComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent evt) {
                periodsComboBoxItemStateChanged(evt);
            }
        });
        panel.add(periodsComboBox, CC.xy(1, 1));

        removeButton = new JButton();
        removeButton.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/remove32.png"))); // NOI18N
        removeButton.setText(getString("Button.Remove")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        panel.add(removeButton, CC.xy(1, 2));

        return panel;
    }

    /**
     * Creates a buttons panel.
     * 
     * @return the created panel
     */
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());
        okButton.setPreferredSize(cancelButton.getPreferredSize());

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }

    private void periodsComboBoxItemStateChanged(ItemEvent evt) {
        updateGUI();
    }

    private void removeButtonActionPerformed(ActionEvent evt) {
        try {
            freezesService.remove(freezesTableModel.getFreezeAt(freezesTable.getSelectedRow()).getId());
        } catch (SecurityException | ValidationException ex) {
            setResult(Result.EXCEPTION);
            setException(new RuntimeException(ex));
            dispose();
            return;
        } catch (BusinessException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        }

        updateGUI();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateGUI();
            setLocationRelativeTo(getParent());
        }

        super.setVisible(visible);
    }

    private void updateGUI() {
        List<FreezeDTO> freezes;
        DateMidnight today = new DateMidnight();
        try {
            if (periodsComboBox.getSelectedIndex() == 0) {
                freezes = freezesService.findByDateIssuedRange(today.minusDays(7), today);
            } else if (periodsComboBox.getSelectedIndex() == 1) {
                freezes = freezesService.findByDateIssuedRange(today.minusMonths(1), today);
            } else if (periodsComboBox.getSelectedIndex() == 2) {
                freezes = freezesService.findByDateIssuedRange(today.minusMonths(3), today);
            } else {
                freezes = freezesService.findAll();
            }
        } catch (SecurityException | ValidationException ex) {
            setResult(Result.EXCEPTION);
            setException(new RuntimeException(ex));
            dispose();
            return;
        }

        freezesTableModel.setFreezes(freezes);
    }
    /*
     * GUI
     */
    private FreezesTableModel freezesTableModel;
    /*
     * Business
     */
    private FreezesService freezesService;
    private JButton cancelButton;
    private JTable freezesTable;
    private JTextArea noteTextArea;
    private JButton okButton;
    private JComboBox periodsComboBox;
    private JButton removeButton;
}
