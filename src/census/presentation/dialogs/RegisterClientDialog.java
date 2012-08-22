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

import census.business.ClientProfilesService;
import census.business.ClientsService;
import census.business.SessionsService;
import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.ClientDTO;
import census.business.dto.ClientProfileDTO;
import census.presentation.MainFrame;
import census.presentation.forms.ClientForm;
import census.presentation.forms.ClientProfileForm;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Danylo Vashchilenko
 */
public class RegisterClientDialog extends CensusDialog {

    /**
     * Creates new form RegisterClientDialog
     */
    public RegisterClientDialog(JFrame parent) {
        super(parent, true);

        clientsService = ClientsService.getInstance();
        client = clientsService.getTemplateClient();
        isClientRegistered = false;

        clientProfilesService = ClientProfilesService.getInstance();
        clientProfile = new ClientProfileDTO();

        buildDialog();


    }

    private void buildDialog() {

        setLayout(new FormLayout("4dlu, p, 4dlu, d, 4dlu", "4dlu, d, 4dlu, d, 4dlu, d, 4dlu"));

        clientPanel = new ClientForm();
        clientPanel.setClient(client);
        clientPanel.setBorder(BorderFactory.createTitledBorder(getString("Text.BasicInformation"))); // NOI18N
        add(clientPanel, CC.xy(2, 2));

        clientProfilePanel = new ClientProfileForm();
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
                // TODO: security check
                Short clientId = clientsService.registerClient(client, SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL));
                client.setId(clientId);
                isClientRegistered = true;
            }
            if (attachProfileCheckBox.isSelected()) {
                clientProfile.setClientId(client.getId());
                clientProfilesService.updateClientProfile(clientProfile);
            }
        } catch (SecurityException ex) {
            // Should not be thrown with current implemetation
            setResult(EditOrderDialog.RESULT_EXCEPTION);
            setException(new RuntimeException(ex));
            dispose();
            return;
        } catch (ValidationException ex) {
            MainFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
            return;
        } catch (BusinessException ex) {
            MainFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(EditOrderDialog.RESULT_EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setClientId(client.getId());
        setEditFinancialActivityDialogRequested(openOrderCheckBox.isSelected());
        setOpenAttendanceDialogRequested(openAttendanceCheckBox.isSelected());
        setResult(CensusDialog.RESULT_OK);
        dispose();
    }

    private void attachProfileCheckBoxStateChanged(ChangeEvent evt) {
        if (attachProfileCheckBox.isSelected() != clientProfilePanel.isVisible()) {
            clientProfilePanel.setVisible(attachProfileCheckBox.isSelected());
            pack();
        }
    }

    public Short getClientId() {
        return clientId;
    }

    public void setClientId(Short clientId) {
        this.clientId = clientId;
    }

    public Boolean isOpenAttendanceDialogRequested() {
        return openAttendanceDialogRequested;
    }

    public void setOpenAttendanceDialogRequested(Boolean attendanceDialogRequested) {
        this.openAttendanceDialogRequested = attendanceDialogRequested;
    }

    public Boolean isEditFinancialActivityDialogRequested() {
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
    private Short clientId;
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
    private ClientForm clientPanel;
    private ClientProfileForm clientProfilePanel;
    private JButton okButton;
    private JCheckBox openAttendanceCheckBox;
    private JCheckBox openOrderCheckBox;
}
