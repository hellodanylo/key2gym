/*
 * Copyright 2012-2013 Danylo Vashchilenko
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.key2gym.business.api.SecurityRoles;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.UserException;
import org.key2gym.business.api.dtos.ClientDTO;
import org.key2gym.business.api.dtos.ClientProfileDTO;
import org.key2gym.business.api.services.AdministratorsService;
import org.key2gym.business.api.services.ClientProfilesService;
import org.key2gym.business.api.services.ClientsService;
import org.key2gym.client.ContextManager;
import org.key2gym.client.UserExceptionHandler;
import org.key2gym.client.panels.forms.ClientFormPanel;
import org.key2gym.client.panels.forms.ClientProfileFormPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 *
 * @author Danylo Vashchilenko
 */
public class RegisterClientDialog extends AbstractDialog {

    /**
     * Creates new form RegisterClientDialog
     */
    public RegisterClientDialog(JFrame parent) throws SecurityViolationException {
        super(parent, true);

        clientsService = ContextManager.lookup(ClientsService.class);
        client = clientsService.getTemplateClient();
        isClientRegistered = false;

        clientProfilesService = ContextManager.lookup(ClientProfilesService.class);
        clientProfile = new ClientProfileDTO();

        buildDialog();


    }

    private void buildDialog() throws SecurityViolationException {

        setLayout(new FormLayout("4dlu, p, 4dlu, d, 4dlu", "4dlu, d, 4dlu, d, 4dlu, d, 4dlu"));

        List<ClientFormPanel.Column> columnsList = Arrays.asList(
                ClientFormPanel.Column.ID,
                ClientFormPanel.Column.FULL_NAME,
                ClientFormPanel.Column.CARD,
                ClientFormPanel.Column.REGISTRATION_DATE,
                ClientFormPanel.Column.MONEY_BALANCE,
                ClientFormPanel.Column.ATTENDANCES_BALANCE,
                ClientFormPanel.Column.EXPIRATION_DATE,
                ClientFormPanel.Column.NOTE);
        clientPanel = new ClientFormPanel(columnsList);
        clientPanel.setClient(client);
        clientPanel.setBorder(BorderFactory.createTitledBorder(getString("Text.BasicInformation"))); // NOI18N
        add(clientPanel, CC.xy(2, 2));

        clientProfilePanel = new ClientProfileFormPanel();
        clientProfilePanel.setClientProfile(clientProfile);
        clientProfilePanel.setBorder(BorderFactory.createTitledBorder(getString("Text.ProfileInformation"))); // NOI18N
        clientProfilePanel.setPreferredSize(new Dimension(400, 400));
        add(clientProfilePanel, CC.xywh(4, 2, 1, 3));

        add(createOptionsPanel(), CC.xy(2, 4));
        add(createButtonsPanel(), CC.xy(2, 6));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.RegisterClient")); // NOI18N
        setResizable(false);
        getRootPane().setDefaultButton(okButton);
        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new FormLayout("l:d", "d, 3dlu, d, 3dlu, d"));
        panel.setBorder(BorderFactory.createTitledBorder(getString("Text.AdditionalActions"))); // NOI18N

        attachProfileCheckBox = new JCheckBox();
        attachProfileCheckBox.setText(getString("CheckBox.AttackProfileNow")); // NOI18N
        attachProfileCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                attachProfileCheckBoxStateChanged(evt);
            }
        });
        panel.add(attachProfileCheckBox, CC.xy(1, 1));

        openOrderCheckBox = new JCheckBox();
        openOrderCheckBox.setText(getString("CheckBox.OpenOrderUponCompletion")); // NOI18N
        panel.add(openOrderCheckBox, CC.xy(1, 3));

        openAttendanceCheckBox = new JCheckBox();
        openAttendanceCheckBox.setText(getString("CheckBox.OpenAttendanceUponCompletion")); // NOI18N
        panel.add(openAttendanceCheckBox, CC.xy(1, 5));

        attachProfileCheckBoxStateChanged(null);

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());
        okButton.setPreferredSize(cancelButton.getPreferredSize());

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }

    @Override
    protected void onOkActionPerformed(ActionEvent evt) {

        if (!clientPanel.trySave() || (attachProfileCheckBox.isSelected() && !clientProfilePanel.trySave())) {
            return;
        }

        try {
            if (!isClientRegistered) {

                /*
                 * Confirms no card during the registration.
                 */
                if(client.getCard() == null) {
                    int result = JOptionPane.showConfirmDialog(this, getString("Message.ConfirmNoCardDuringRegistration"),
                            getString("Title.RegisterClient"), JOptionPane.YES_NO_OPTION);

                    if(result == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                Integer clientId = clientsService.registerClient(client, 
								 ContextManager.lookup(AdministratorsService.class)
								 .getCurrent().getRoles().contains(SecurityRoles.MANAGER));
                client.setId(clientId);
                isClientRegistered = true;
            }
            if (attachProfileCheckBox.isSelected()) {
                clientProfile.setClientId(client.getId());
                clientProfilesService.updateClientProfile(clientProfile);
            }
        } catch (UserException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        }

        setClientId(client.getId());
        setEditFinancialActivityDialogRequested(openOrderCheckBox.isSelected());
        setOpenAttendanceDialogRequested(openAttendanceCheckBox.isSelected());
        setResult(Result.OK);
        dispose();
    }

    private void attachProfileCheckBoxStateChanged(ChangeEvent evt) {
        if (attachProfileCheckBox.isSelected() != clientProfilePanel.isVisible()) {
            clientProfilePanel.setVisible(attachProfileCheckBox.isSelected());
            pack();
        }
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Boolean isOpenAttendanceDialogRequested() {
        return openAttendanceDialogRequested;
    }

    public void setOpenAttendanceDialogRequested(Boolean attendanceDialogRequested) {
        this.openAttendanceDialogRequested = attendanceDialogRequested;
    }

    public Boolean isEditOrderDialogRequested() {
        return editFinancialActivityDialogRequested;
    }

    public void setEditFinancialActivityDialogRequested(Boolean editFinancialActivityDialogRequested) {
        this.editFinancialActivityDialogRequested = editFinancialActivityDialogRequested;
    }
    /*
     * Session variables
     */
    private Boolean editFinancialActivityDialogRequested;
    private Boolean openAttendanceDialogRequested;
    private Integer clientId;
    /*
     * Business
     */
    private ClientsService clientsService;
    private ClientDTO client;
    private Boolean isClientRegistered;
    private ClientProfilesService clientProfilesService;
    private ClientProfileDTO clientProfile;

    /*
     * Components
     */
    private JCheckBox attachProfileCheckBox;
    private JButton cancelButton;
    private ClientFormPanel clientPanel;
    private ClientProfileFormPanel clientProfilePanel;
    private JButton okButton;
    private JCheckBox openAttendanceCheckBox;
    private JCheckBox openOrderCheckBox;
}
