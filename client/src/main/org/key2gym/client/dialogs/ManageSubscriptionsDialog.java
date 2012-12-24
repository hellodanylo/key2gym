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
package org.key2gym.client.dialogs;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.UserException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.SubscriptionDTO;
import org.key2gym.business.api.remote.SubscriptionsServiceRemote;
import org.key2gym.client.ContextManager;
import org.key2gym.client.UserExceptionHandler;
import org.key2gym.client.factories.FormPanelDialogsFactory;
import org.key2gym.client.util.SubscriptionsTableModel;
import org.key2gym.client.util.SubscriptionsTableModel.Column;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageSubscriptionsDialog extends AbstractDialog {

    /**
     * Creates new form ItemsDialog
     */
    public ManageSubscriptionsDialog(JFrame parent) throws SecurityViolationException {
        super(parent, true);

        subscriptionsService = ContextManager.lookup(SubscriptionsServiceRemote.class);
        subscriptions = subscriptionsService.getAllSubscriptions();

        initComponents();
        buildDialog();
    }

    private void initComponents() {

        /*
         * Subscriptions table
         */
        subscriptionsScrollPane = new JScrollPane();
        subscriptionsTable = new JTable();
        Column[] columns = new Column[]{
            Column.TITLE,
            Column.PRICE,
            Column.UNITS,
            Column.TIME_RANGE,
            Column.TERM_DAYS,
            Column.TERM_MONTHS,
            Column.TERM_YEARS
        };
        subscriptionsTableModel = new SubscriptionsTableModel(columns);
        subscriptionsTableModel.setSubscriptions(subscriptions);
        subscriptionsTable.setModel(subscriptionsTableModel);
        subscriptionsScrollPane.setViewportView(subscriptionsTable);

        int[] widths = new int[]{200, 50, 50, 137, 26, 26, 26};
        TableColumn column;
        for (int i = 0; i < columns.length; i++) {
            column = subscriptionsTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }

        /*
         * Add button
         */
        addButton = new JButton();
        addButton.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/plus32.png"))); // NOI18N
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
        editButton.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/edit32.png"))); // NOI18N
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
        removeButton.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/remove32.png"))); // NOI18N
        removeButton.setText(getString("Button.Remove")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        /*
         * Listens to the table to know when to enable the edit button
         */
        subscriptionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                onSubscriptionsTableSelectionChanged();
            }
        });
        onSubscriptionsTableSelectionChanged();

        /*
         * Close button
         */
        closeButton = new JButton();
        closeButton.setAction(getCloseAction());
        getRootPane().setDefaultButton(closeButton);
    }

    private void buildDialog() {

        FormLayout layout = new FormLayout("4dlu, [400dlu, p]:g, 4dlu, p, 4dlu",
                "4dlu, f:[200dlu, p]:g, 4dlu");
        setLayout(layout);

        add(subscriptionsScrollPane, CC.xy(2, 2));

        JPanel buttonsPanel = new JPanel();
        {
            buttonsPanel.setLayout(new FormLayout("d", "b:d:g, c:d, t:d:g, d"));
            buttonsPanel.add(addButton, CC.xy(1, 1));
            buttonsPanel.add(editButton, CC.xy(1, 2));
            buttonsPanel.add(removeButton, CC.xy(1, 3));
            buttonsPanel.add(closeButton, CC.xy(1, 4));
        }
        add(buttonsPanel, CC.xy(4, 2));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.ManageSubscriptions")); // NOI18N
        pack();
        setMinimumSize(getPreferredSize());
        setResizable(true);
        setLocationRelativeTo(getParent());
    }

    private void addOrEditButtonActionPerformed(ActionEvent evt) {

	SubscriptionDTO subscription;

        if (evt.getSource().equals(addButton)) {
	    subscription = new SubscriptionDTO();
	} else {
	    subscription = subscriptions.get(subscriptionsTable.getSelectedRow());
        }

	AbstractDialog dialog = FormPanelDialogsFactory.createSubscriptionEditor(this, subscription);

        dialog.setVisible(true);

	try {
	    subscriptions = subscriptionsService.getAllSubscriptions();
	} catch (UserException ex) {
	    UserExceptionHandler.getInstance().processException(ex);
	    return;
	}
	
	subscriptionsTableModel.setSubscriptions(subscriptions);
    }

    private void removeButtonActionPerformed(ActionEvent evt) {
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, getString("Message.AreYouSureYouWantToRemoveItems"), getString("Title.Confirmation"), JOptionPane.YES_NO_OPTION)) {
            return;
        }

        for (int index : subscriptionsTable.getSelectedRows()) {

            try {
                subscriptionsService.removeSubscription(subscriptions.get(index).getId());
            } catch (ValidationException | BusinessException | SecurityViolationException ex) {
                UserExceptionHandler.getInstance().processException(ex);
            }
        }

        try {
            subscriptions = subscriptionsService.getAllSubscriptions();
        } catch (SecurityViolationException ex) {
            throw new RuntimeException(ex);
        }
        
        subscriptionsTableModel.setSubscriptions(subscriptions);
    }

    private void onSubscriptionsTableSelectionChanged() {
        if (subscriptionsTable.getSelectedRowCount() == 0) {
            removeButton.setEnabled(false);
            editButton.setEnabled(false);
        } else if (subscriptionsTable.getSelectedRowCount() == 1) {
            editButton.setEnabled(true);
            removeButton.setEnabled(true);
        } else {
            removeButton.setEnabled(true);
            editButton.setEnabled(false);
        }
    }
    /*
     * Business
     */
    private List<SubscriptionDTO> subscriptions;
    private SubscriptionsServiceRemote subscriptionsService;
    /*
     * Presentation
     */
    private SubscriptionsTableModel subscriptionsTableModel;
    private JButton addButton;
    private JButton editButton;
    private JScrollPane subscriptionsScrollPane;
    private JTable subscriptionsTable;
    private JButton closeButton;
    private JButton removeButton;
}
