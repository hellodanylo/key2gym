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

import census.business.AttendancesService;
import census.business.KeysService;
import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.business.dto.KeyDTO;
import census.presentation.util.KeyListCellRenderer;
import census.presentation.util.UserExceptionHandler;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PickAttendanceDialog extends AbstractDialog {

    /**
     * Creates new PickAttendanceDialog
     */
    public PickAttendanceDialog(JFrame parent) throws BusinessException {
        super(parent, true);

        attendanceId = null;
        keyId = null;
        attendanceLocked = null;
        editOrderDialogRequested = false;

        initComponents();
        setLocationRelativeTo(parent);

    }

    private void initComponents() {
        
        setLayout(new FormLayout("4dlu, l:d, 3dlu, d:g, 4dlu", "4dlu, d:g, 3dlu, d, 4dlu, d, 4dlu"));
        
        add(new JLabel(getString("Label.Key")), CC.xy(2,2));
        add(createKeysComboBox(), CC.xy(4, 2));
        add(createOptionsPanel(), CC.xywh(2, 4, 3, 1));
        add(createButtonsPanel(), CC.xywh(2, 6, 3 ,1));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.PickAttendance")); // NOI18N
        setResizable(false);
        getRootPane().setDefaultButton(okButton);
        pack();
    }

    private JComboBox createKeysComboBox() {
        
        keysComboBox = new JComboBox();

        /*
         * Gets keys taken.
         */
        List<KeyDTO> keys = KeysService.getInstance().getKeysTaken();
        if (keys.isEmpty()) {
            UserExceptionHandler.getInstance().processException(new BusinessException(getString("Message.NoAttendanceIsOpen")));
            getCancelAction().actionPerformed(null);
        }
        
        keysComboBox.setRenderer(new KeyListCellRenderer());
        keysComboBox.setModel(new DefaultComboBoxModel(keys.toArray()));

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
            AttendancesService attendancesService = AttendancesService.getInstance();
            Boolean keyFound = false;

            for (KeyDTO key : keys) {
                Short attendanceId;
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

        if (isEditOrderDialogRequested()) {
            openOrderCheckBox.doClick();
        }
        
        return keysComboBox;
    }
    
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(getString("Text.AdditionalActions"))); // NOI18N
        openOrderCheckBox = new JCheckBox();
        openOrderCheckBox.setSelected(true);
        openOrderCheckBox.setText(getString("CheckBox.OpenOrder")); // NOI18N
        
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
    
    @Override
    protected void onOkActionPerformed(ActionEvent evt) {
        AttendancesService attendancesService;
        Short keyId;
        Short attendanceId;

        try {
            attendancesService = AttendancesService.getInstance();
            keyId = ((KeyDTO) keysComboBox.getSelectedItem()).getId();
            try {
                attendanceId = attendancesService.findOpenAttendanceByKey(keyId);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }
        } catch (BusinessException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(AbstractDialog.RESULT_EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setAttendanceId(attendanceId);
        setKeyId(keyId);
        setEditOrderDialogRequested(openOrderCheckBox.isSelected());
        super.onOkActionPerformed(evt);
    }

    public Short getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Short attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Short getKeyId() {
        return keyId;
    }

    public void setKeyId(Short keyId) {
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
     * Session variables
     */
    private Boolean attendanceLocked;
    private Short attendanceId;
    private Short keyId;
    private Boolean editOrderDialogRequested;
    /*
     * Components
     */
    private JButton cancelButton;
    private JComboBox keysComboBox;
    private JButton okButton;
    private JCheckBox openOrderCheckBox;                
}
