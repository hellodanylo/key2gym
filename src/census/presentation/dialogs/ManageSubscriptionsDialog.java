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
package census.presentation.dialogs;

import census.presentation.dialogs.editors.SubscriptionEditorDialog;
import census.business.SubscriptionsService;
import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.SubscriptionDTO;
import census.presentation.CensusFrame;
import census.presentation.util.SubscriptionsTableModel;
import census.presentation.util.SubscriptionsTableModel.Column;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageSubscriptionsDialog extends CensusDialog {

    /**
     * Creates new form ItemsDialog
     */
    public ManageSubscriptionsDialog(JFrame parent) {
        super(parent, true);
        subscriptions = SubscriptionsService.getInstance().getAllSubscriptions();

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
        
        int[] widths = new int[]{200,50,50,137,26,26,26};
        TableColumn column;
        for (int i = 0; i < columns.length; i++) {
            column = subscriptionsTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }

        /*
         * Add button
         */
        addButton = new JButton();
        // TODO: is the trailing / necessary?
        addButton.setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/plus32.png"))); // NOI18N
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
        editButton.setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/edit32.png"))); // NOI18N
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
        removeButton.setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/remove32.png"))); // NOI18N
        removeButton.setText(getString("Button.Remove")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        getRootPane().setDefaultButton(okButton);

        /*
         * Listens to the table to know when to enable the edit button
         */
        subscriptionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                // There has to be exactly one selected item for editing to take place
                if (subscriptionsTable.getSelectedRowCount() == 1) {
                    editButton.setEnabled(true);
                } else {
                    editButton.setEnabled(false);
                }
            }
        });

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

        FormLayout layout = new FormLayout("4dlu, default:grow, 4dlu, default, 4dlu",
                "4dlu, bottom:default:grow, center:default, top:default:grow, default, 4dlu, default, 4dlu");
        setLayout(layout);

        add(subscriptionsScrollPane, CC.xywh(2, 2, 1, 6));

        add(addButton, CC.xy(4, 2));
        add(editButton, CC.xy(4, 3));
        add(removeButton, CC.xy(4, 4));
        add(okButton, CC.xy(4, 5));
        add(cancelButton, CC.xy(4, 7));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.ManageSubscriptions")); // NOI18N
        setResizable(true);
        setSize(new Dimension(850, 480));
        setLocationRelativeTo(getParent());
    }

    private void addOrEditButtonActionPerformed(ActionEvent evt) {

        CensusDialog dialog;

        if (evt.getSource().equals(addButton)) {
            dialog = new SubscriptionEditorDialog(new SubscriptionDTO());
        } else {
            dialog = new SubscriptionEditorDialog(subscriptions.get(subscriptionsTable.getSelectedRow()));
        }

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getResult().equals(CensusDialog.RESULT_OK)) {
            subscriptions = SubscriptionsService.getInstance().getAllSubscriptions();
            subscriptionsTableModel.setSubscriptions(subscriptions);
        } else if(dialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
            setResult(RESULT_EXCEPTION);
            setException(dialog.getException());
            dispose();
            return;
        }
    }

    private void removeButtonActionPerformed(ActionEvent evt) {
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, getString("Message.AreYouSureYouWantToRemoveItems"), getString("Title.Confirmation"), JOptionPane.YES_NO_OPTION)) {
            return;
        }

        SubscriptionsService subscriptionsService = SubscriptionsService.getInstance();

        for (int index : subscriptionsTable.getSelectedRows()) {
            try {
                try {
                    subscriptionsService.removeSubscription(subscriptions.get(index).getId());
                } catch (ValidationException | SecurityException ex) {
                    throw new RuntimeException(ex);
                } catch (BusinessException ex) {
                    CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
                }
            } catch (RuntimeException ex) {
                /*
                 * The exception is unexpected. We got to shutdown the dialog
                 * for the state of the transaction is now unknown.
                 */
                setResult(EditOrderDialog.RESULT_EXCEPTION);
                setException(ex);
                dispose();
                return;
            }
        }

        subscriptions = subscriptionsService.getAllSubscriptions();
        subscriptionsTableModel.setSubscriptions(subscriptions);
    }

    /*
     * Business
     */
    private List<SubscriptionDTO> subscriptions;

    /*
     * Presentation
     */
    private SubscriptionsTableModel subscriptionsTableModel;
    private JButton addButton;
    private JButton cancelButton;
    private JButton editButton;
    private JScrollPane subscriptionsScrollPane;
    private JTable subscriptionsTable;
    private JButton okButton;
    private JButton removeButton;
}
