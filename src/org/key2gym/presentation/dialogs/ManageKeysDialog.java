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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.key2gym.business.KeysService;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.KeyDTO;
import org.key2gym.presentation.factories.FormPanelDialogsFactory;
import org.key2gym.presentation.util.KeysTableModel;
import org.key2gym.presentation.util.KeysTableModel.Column;
import org.key2gym.presentation.util.UserExceptionHandler;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageKeysDialog extends AbstractDialog {

    /**
     * Creates new ManageKeysDialog.
     */
    public ManageKeysDialog(JFrame parent) {
        super(parent, true);
        keys = KeysService.getInstance().getAllKeys();

        initComponents();
        buildDialog();
    }

    private void initComponents() {

        /*
         * Keys table
         */
        keysScrollPane = new JScrollPane();
        keysTable = new JTable();
        Column[] columns =
                new Column[]{Column.ID, Column.TITLE};
        keysTableModel = new KeysTableModel(columns);
        keysTableModel.setKeys(keys);
        keysTable.setModel(keysTableModel);
        keysScrollPane.setViewportView(keysTable);

        /*
         * Add button
         */
        addButton = new JButton();
        addButton.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/plus32.png"))); // NOI18N
        addButton.setText(getString("Button.Add")); // NOI18N
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrEditButtonActionPerformed(evt);
            }
        });

        /*
         * Edit button
         */
        editButton = new JButton();
        editButton.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/edit32.png"))); // NOI18N
        editButton.setText(getString("Button.Edit")); // NOI18N
        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrEditButtonActionPerformed(evt);
            }
        });

        /*
         * Remove button
         */
        removeButton = new JButton();
        removeButton.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/remove32.png"))); // NOI18N
        removeButton.setText(getString("Button.Remove")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        getRootPane().setDefaultButton(okButton);

        /*
         * Listens to the table to know when to enable the edit button
         */
        keysTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                onKeysTableSelectionChanged();
            }
        });
        onKeysTableSelectionChanged();
        
        /*
         * Ok button
         */
        okButton = new JButton();
        okButton.setAction(getOkAction());

        /*
         * Cancel button
         */
        cancelButton = new JButton();
        cancelButton.setAction(getCancelAction());
    }

    private void buildDialog() {
        FormLayout layout = new FormLayout("4dlu, [400dlu, p]:g, 4dlu, p, 4dlu",
                "4dlu, f:[200dlu, p]:g, 4dlu");
        setLayout(layout);

        add(keysScrollPane, CC.xy(2, 2));

        JPanel buttonsPanel = new JPanel();
        {
            buttonsPanel.setLayout(new FormLayout("d", "b:d:g, c:d, t:d:g, d, d"));
            buttonsPanel.add(addButton, CC.xy(1, 1));
            buttonsPanel.add(editButton, CC.xy(1, 2));
            buttonsPanel.add(removeButton, CC.xy(1, 3));
            buttonsPanel.add(okButton, CC.xy(1, 4));
            buttonsPanel.add(cancelButton, CC.xy(1, 5));
        }
        add(buttonsPanel, CC.xy(4, 2));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.ManageItems")); // NOI18N
        pack();
        setMinimumSize(getPreferredSize());
        setResizable(true);
        setLocationRelativeTo(getParent());
    }

    private void onKeysTableSelectionChanged() {
        // There has to be exactly one selected item for editing to take place
        if (keysTable.getSelectedRowCount() == 1) {
            editButton.setEnabled(true);
        } else {
            editButton.setEnabled(false);
        }

        if (keysTable.getSelectedRowCount() == 0) {
            removeButton.setEnabled(false);
        } else {
            removeButton.setEnabled(true);
        }
    }

    private void addOrEditButtonActionPerformed(ActionEvent evt) {

        KeyDTO key;

        if (evt.getSource().equals(addButton)) {
            key = new KeyDTO();
        } else {
            key = keys.get(keysTable.getSelectedRow());
        }

        FormDialog dialog = FormPanelDialogsFactory.createKeyEditor(this, key);

        dialog.setVisible(true);

        if (dialog.getResult().equals(FormDialog.Result.OK)) {
            keys = KeysService.getInstance().getAllKeys();
            keysTableModel.setKeys(keys);
        } else if (dialog.getResult().equals(FormDialog.Result.EXCEPTION)) {
            setResult(AbstractDialog.Result.EXCEPTION);
            setException(dialog.getException());
            dispose();
            return;
        }
    }

    private void removeButtonActionPerformed(ActionEvent evt) {
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, getString("Message.AreYouSureYouWantToRemoveItems"), getString("Title.Confirmation"), JOptionPane.YES_NO_OPTION)) {
            return;
        }

        KeysService keysService = KeysService.getInstance();

        for (int index : keysTable.getSelectedRows()) {
            try {
                try {
                    keysService.removeKey(keys.get(index).getId());
                } catch (SecurityException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (ValidationException ex) {
                UserExceptionHandler.getInstance().processException(ex);
                return;
            } catch (RuntimeException ex) {
                /*
                 * The exception is unexpected. We got to shutdown the dialog
                 * for the state of the transaction is now unknown.
                 */
                setResult(EditOrderDialog.Result.EXCEPTION);
                setException(ex);
                dispose();
                return;
            }
        }

        keys = keysService.getAllKeys();
        keysTableModel.setKeys(keys);
    }

    /*
     * Business
     */
    private List<KeyDTO> keys;

    /*
     * Presentation
     */
    private KeysTableModel keysTableModel;
    private JButton addButton;
    private JButton cancelButton;
    private JButton editButton;
    private JScrollPane keysScrollPane;
    private JTable keysTable;
    private JButton okButton;
    private JButton removeButton;
}
