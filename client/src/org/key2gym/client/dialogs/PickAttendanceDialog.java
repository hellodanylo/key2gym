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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.AttendanceDTO;
import org.key2gym.business.api.dtos.ClientDTO;
import org.key2gym.business.api.dtos.KeyDTO;
import org.key2gym.business.api.remote.AttendancesServiceRemote;
import org.key2gym.business.api.remote.ClientsServiceRemote;
import org.key2gym.business.api.remote.KeysServiceRemote;
import org.key2gym.client.ContextManager;
import org.key2gym.client.UserExceptionHandler;
import org.key2gym.client.panels.forms.ClientFormPanel;
import org.key2gym.client.panels.forms.ClientFormPanel.Column;
import org.key2gym.client.renderers.KeyListCellRenderer;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PickAttendanceDialog extends AbstractDialog {

    /**
     * Creates new PickAttendanceDialog
     */
    public PickAttendanceDialog(JFrame parent) throws BusinessException, SecurityViolationException {
        super(parent, true);
        
        attendancesService = ContextManager.lookup(AttendancesServiceRemote.class);
        keysService = ContextManager.lookup(KeysServiceRemote.class);
        clientsService = ContextManager.lookup(ClientsServiceRemote.class);

        attendanceId = null;
        keyId = null;
        attendanceLocked = null;
        editOrderDialogRequested = false;

        initComponents();
        setLocationRelativeTo(parent);

    }

    private void initComponents() throws BusinessException, SecurityViolationException {

        setLayout(new FormLayout("4dlu, l:d, 3dlu, [150dlu, d]:g, 4dlu", "4dlu, d:g, 3dlu, d, 3dlu, d, 4dlu, d, 4dlu"));

        add(new JLabel(getString("Label.Key")), CC.xy(2, 2));
        add(createKeysComboBox(), CC.xy(4, 2));
        add(createClientPanel(), CC.xywh(2, 4, 3, 1));
        add(createOptionsPanel(), CC.xywh(2, 6, 3, 1));
        add(createButtonsPanel(), CC.xywh(2, 8, 3, 1));
        
        // Updates the client panel
        onSelectedKeyChanged();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.PickAttendance")); // NOI18N
        setResizable(false);
        getRootPane().setDefaultButton(okButton);
        pack();
    }

    private JComboBox createKeysComboBox() throws BusinessException, SecurityViolationException {

        keysComboBox = new JComboBox();

        /*
         * Gets all keys taken.
         */
        List<KeyDTO> keys = keysService.getKeysTaken();
        
        if (keys.isEmpty()) {
            throw new BusinessException(getString("Message.NoAttendanceIsOpen"));
        }

        keysComboBox.setRenderer(new KeyListCellRenderer());
        keysComboBox.setModel(new DefaultComboBoxModel(keys.toArray()));
        keysComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                onSelectedKeyChanged();
            }
        });

        if (getKeyId() != null) {
            /*
             * If the session contains a key's ID, finds the key and uses it.
             */
            for (KeyDTO key : keys) {
                if (key.getId().equals(getKeyId())) {
                    keysComboBox.setSelectedItem(key);
                    keysComboBox.setEnabled(!isAttendanceLocked());
                    break;
                }
            }
        } else if (getAttendanceId() != null) {
            /*
             * If the session contains an attendance's ID, finds the key
             * assigned to the attendance and uses it.
             */
            Boolean keyFound = false;

            for (KeyDTO key : keys) {
                Integer attendanceId;
                try {
                    attendanceId = attendancesService.findOpenAttendanceByKey(key.getId());
                } catch (ValidationException | BusinessException ex) {
                    /*
                     * The exception is unexpected. We got to shutdown the
                     * dialog for the state of the transaction is now unknown.
                     */
                    throw new RuntimeException(ex);
                }

                if (attendanceId.equals(getAttendanceId())) {
                    keysComboBox.setSelectedItem(key);
                    keysComboBox.setEnabled(!isAttendanceLocked());
                    setKeyId(key.getId());
                    keyFound = true;
                    break;
                }
            }

            if (!keyFound) {
                throw new RuntimeException("Can not find appropriate key.");
            }
        }

        return keysComboBox;
    }

    private JPanel createClientPanel() {
        clientPanel = new ClientFormPanel(Arrays.asList(Column.FULL_NAME, Column.MONEY_BALANCE, Column.ATTENDANCES_BALANCE, Column.EXPIRATION_DATE));
        clientPanel.setBorder(BorderFactory.createTitledBorder(getString("Text.BillingInformation")));
        clientPanel.setEditable(false);
        return clientPanel;
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(getString("Text.AdditionalActions"))); // NOI18N
        openOrderCheckBox = new JCheckBox();
        openOrderCheckBox.setSelected(true);
        openOrderCheckBox.setText(getString("CheckBox.OpenOrder")); // NOI18N

        if (isEditOrderDialogRequested()) {
            openOrderCheckBox.doClick();
        }

        panel.add(openOrderCheckBox);

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

    private void onSelectedKeyChanged() {
        KeyDTO key = (KeyDTO) keysComboBox.getSelectedItem();

        if (key == null) {
            clientPanel.setClient(null);
            return;
        }

        ClientDTO client;
        AttendanceDTO attendance;

        try {
            attendance = attendancesService.getAttendanceById(attendancesService.findOpenAttendanceByKey(key.getId()));

            if (attendance.getClientId() != null) {
                client = clientsService.getById(attendance.getClientId());
            } else {
                client = null;
            }
        } catch (ValidationException | BusinessException | SecurityViolationException ex) {
            setResult(Result.EXCEPTION);
            setException(new RuntimeException(ex));
            dispose();
            return;
        }
        
        clientPanel.setClient(client);
    }

    @Override
    protected void onOkActionPerformed(ActionEvent evt) {
        Integer keyId;
        Integer attendanceId;

        try {
            keyId = ((KeyDTO) keysComboBox.getSelectedItem()).getId();
            try {
                attendanceId = attendancesService.findOpenAttendanceByKey(keyId);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }
        } catch (BusinessException | SecurityViolationException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(AbstractDialog.Result.EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setAttendanceId(attendanceId);
        setKeyId(keyId);
        setEditOrderDialogRequested(openOrderCheckBox.isSelected());
        super.onOkActionPerformed(evt);
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Integer getKeyId() {
        return keyId;
    }

    public void setKeyId(Integer keyId) {
        this.keyId = keyId;
    }

    public Boolean isAttendanceLocked() {
        return attendanceLocked;
    }

    public void setAttendanceLocked(Boolean attendanceLocked) {
        this.attendanceLocked = attendanceLocked;
    }

    public Boolean isEditOrderDialogRequested() {
        return editOrderDialogRequested;
    }

    public void setEditOrderDialogRequested(Boolean requested) {
        this.editOrderDialogRequested = requested;
    }
    /*
     * Business
     */
    private KeysServiceRemote keysService;
    private AttendancesServiceRemote attendancesService;
    private ClientsServiceRemote clientsService;
    
    /*
     * Session variables
     */
    private Boolean attendanceLocked;
    private Integer attendanceId;
    private Integer keyId;
    private Boolean editOrderDialogRequested;
    /*
     * Components
     */
    private JComboBox keysComboBox;
    private ClientFormPanel clientPanel;
    private JButton cancelButton;
    private JButton okButton;
    private JCheckBox openOrderCheckBox;
}
