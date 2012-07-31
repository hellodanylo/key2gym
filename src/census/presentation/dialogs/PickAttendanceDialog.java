/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.dialogs;

import census.business.AttendancesService;
import census.business.KeysService;
import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.business.dto.KeyDTO;
import census.persistence.Key;
import census.presentation.CensusFrame;
import census.presentation.util.KeyListCellRenderer;
import java.beans.Beans;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;

/**
 *
 * @author daniel
 */
public class PickAttendanceDialog extends CensusDialog {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    /**
     * Creates new form PickAttendanceDialog
     */
    public PickAttendanceDialog(JFrame parent) throws BusinessException {
        super(parent, true);

        attendanceId = null;
        keyId = null;
        attendanceLocked = null;
        editFinancialActivityDialogRequested = false;

        initComponents();
        setLocationRelativeTo(parent);

        if (!Beans.isDesignTime()) {

            /*
             * Gets keys taken.
             */
            List<KeyDTO> keys = KeysService.getInstance().getKeysTaken();
            if (keys.isEmpty()) {
                throw new BusinessException(bundle.getString("Message.NoAttendanceIsOpen"));
            }
            keysComboBox.setRenderer(new KeyListCellRenderer());
            keysComboBox.setModel(new DefaultComboBoxModel(keys.toArray()));

            if (getKeyId() != null) {
                /*
                 * If the session contains a key's ID, finds the key and uses
                 * it.
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
                         * dialog for the state of the transaction is now
                         * unknown.
                         */
                        setResult(RESULT_EXCEPTION);
                        setException(new RuntimeException(ex));
                        dispose();
                        return;
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
                    setResult(RESULT_EXCEPTION);
                    setException(new RuntimeException("Can not find appropriate key."));
                    dispose();
                    return;
                }
            }

            if (isEditFinancialActivityDialogRequested()) {
                openFinancialActivityCheckBox.doClick();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        keyLabel = new javax.swing.JLabel();
        keysComboBox = new javax.swing.JComboBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        optionsPanel = new javax.swing.JPanel();
        openFinancialActivityCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("Title.PickAttendance")); // NOI18N
        setResizable(false);

        keyLabel.setText(bundle.getString("Label.Key")); // NOI18N

        keysComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        getRootPane().setDefaultButton(okButton);
        okButton.setText(bundle.getString("Button.Ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("Button.Cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("Text.AdditionalActions"))); // NOI18N

        openFinancialActivityCheckBox.setSelected(true);
        openFinancialActivityCheckBox.setText(bundle.getString("CheckBox.OpenOrder")); // NOI18N

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(openFinancialActivityCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(openFinancialActivityCheckBox)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(keyLabel)
                        .addGap(4, 4, 4)
                        .addComponent(keysComboBox, 0, 277, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keysComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
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
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(OpenAttendanceDialog.RESULT_EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setResult(CensusDialog.RESULT_OK);
        setAttendanceId(attendanceId);
        setKeyId(keyId);
        setEditFinancialActivityDialogRequested(openFinancialActivityCheckBox.isSelected());
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setResult(RESULT_CANCEL);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    private Boolean attendanceLocked;
    private Short attendanceId;
    private Short keyId;
    private Boolean editFinancialActivityDialogRequested;

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

    public Boolean isEditFinancialActivityDialogRequested() {
        return editFinancialActivityDialogRequested;
    }

    public void setEditFinancialActivityDialogRequested(Boolean editFinancialActivityDialogRequested) {
        this.editFinancialActivityDialogRequested = editFinancialActivityDialogRequested;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel keyLabel;
    private javax.swing.JComboBox keysComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox openFinancialActivityCheckBox;
    private javax.swing.JPanel optionsPanel;
    // End of variables declaration//GEN-END:variables
}
