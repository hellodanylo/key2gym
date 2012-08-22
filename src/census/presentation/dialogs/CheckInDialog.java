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
import census.business.ClientsService;
import census.business.KeysService;
import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.business.dto.KeyDTO;
import census.presentation.util.KeyListCellRenderer;
import census.presentation.util.UserExceptionHandler;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CheckInDialog extends AbstractDialog {

    /**
     * Creates new CheckInDialog
     */
    public CheckInDialog(JFrame parent) throws BusinessException {
        super(parent, true);

        /*
         * Default values for session variables
         */
        orderDialogRequested = false;
        clientLocked = false;

        initComponents();
        buildDialog();
    }

    /**
     * Initializes the dialog's components.
     */
    private void initComponents() throws BusinessException {

        /*
         * Client details panel
         */
        clientDetailsPanel = new JPanel();
        clientDetailsPanel.setBorder(BorderFactory.createTitledBorder(getString("Text.ClientDetails"))); // NOI18N
        clientButtonGroup = new ButtonGroup();
        {
            idRadioButton = new JRadioButton();
            idRadioButton.setText(getString("Text.RegisteredClientWithTheFollowingID")); // NOI18N
            idRadioButton.setActionCommand("clientId");
            idRadioButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    checkBoxesActionPerformed(evt);
                }
            });
            clientButtonGroup.add(idRadioButton);

            idTextField = new JTextField();
            idTextField.setColumns(8);

            cardRadioButton = new JRadioButton();
            cardRadioButton.setText(getString("Text.RegisteredClientWithTheFollowingCard")); // NOI18N
            cardRadioButton.setActionCommand("clientCard");
            cardRadioButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    checkBoxesActionPerformed(evt);
                }
            });
            clientButtonGroup.add(cardRadioButton);

            cardTextField = new JTextField();
            cardTextField.setColumns(8);

            anonymousRadioButton = new JRadioButton();
            anonymousRadioButton.setText(getString("Text.ClientWithNoMembership")); // NOI18N
            anonymousRadioButton.setActionCommand("anonymous"); // NOI18N
            anonymousRadioButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    checkBoxesActionPerformed(evt);
                }
            });
            clientButtonGroup.add(anonymousRadioButton);
        }

        /*
         * Keys
         */
        keyLabel = new JLabel(getString("Label.Key")); // NOI18N
        keysComboBox = new JComboBox();
        List keys = KeysService.getInstance().getKeysAvailable();
        if (keys.isEmpty()) {
            throw new BusinessException(getString("Message.NoKeyIsAvailable"));
        }
        keysComboBox.setRenderer(new KeyListCellRenderer());
        keysComboBox.setModel(new DefaultComboBoxModel(keys.toArray()));
        keysComboBox.setSelectedIndex(0);

        /*
         * Additional actions
         */
        additionalActionsPanel = new JPanel();
        additionalActionsPanel.setBorder(BorderFactory.createTitledBorder(getString("Text.AdditionalActions"))); // NOI18N

        openOrderCheckBox = new JCheckBox();
        openOrderCheckBox.setText(getString("CheckBox.OpenOrder")); // NOI18N

        /*
         * Buttons panel
         */
        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());

        Dimension commonSize = cancelButton.getPreferredSize();
        okButton.setPreferredSize(commonSize);

        getRootPane().setDefaultButton(okButton);

        /*
         * Smart Input (see issue #22).
         */
        addHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, KeyEvent.CTRL_DOWN_MASK), new CardFocusAction());
    }

    /**
     * Builds the dialog by placing components on it.
     */
    private void buildDialog() {
        setContentPane(new JPanel());

        setLayout(new FormLayout("4dlu, f:p:g, 4dlu", "4dlu, p, 3dlu, p, 3dlu, p, 4dlu, p, 4dlu"));

        /*
         * Client details panel
         */
        {
            clientDetailsPanel.setLayout(new FormLayout("f:p:g", "3dlu, p, 3dlu, p, 4dlu, p, 3dlu, p, 4dlu, p, 4dlu"));

            clientDetailsPanel.add(idRadioButton, CC.xy(1, 2));
            clientDetailsPanel.add(idTextField, CC.xy(1, 4));

            clientDetailsPanel.add(cardRadioButton, CC.xy(1, 6));
            clientDetailsPanel.add(cardTextField, CC.xy(1, 8));

            clientDetailsPanel.add(anonymousRadioButton, CC.xy(1, 10));
        }
        add(clientDetailsPanel, CC.xy(2, 2));

        /*
         * Keys panel
         */
        JPanel keysPanel = new JPanel();
        {
            keysPanel.setLayout(new FormLayout("p, 3dlu, p:g", "p"));

            keysPanel.add(keyLabel, CC.xy(1, 1));
            keysPanel.add(keysComboBox, CC.xy(3, 1));
        }
        add(keysPanel, CC.xy(2, 4));

        /*
         * Additional actions
         */
        {
            additionalActionsPanel.setLayout(new FormLayout("4dlu, p:g, 4dlu", "4dlu, p, 4dlu"));

            additionalActionsPanel.add(openOrderCheckBox, CC.xy(2, 2));
        }
        add(additionalActionsPanel, CC.xy(2, 6));

        /*
         * Buttons panel
         */
        JPanel buttonsPanel = new JPanel();
        {
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            buttonsPanel.add(okButton);
            buttonsPanel.add(cancelButton);
        }
        add(buttonsPanel, CC.xy(2, 8));

        setTitle(getString("Title.OpenAttendance")); // NOI18N
        setResizable(false);
        pack();
        setLocationRelativeTo(getParent());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            /*
             * Updates the dialog according to the session variables. We can not
             * do it constructor for the caller then would not have a chance to
             * set the session variables.
             */
            openOrderCheckBox.setSelected(isOrderDialogRequested());
            if (getClientId() != null) {
                idTextField.setText(getClientId().toString());
                idRadioButton.doClick();
                if (isClientLocked()) {
                    idRadioButton.setEnabled(false);
                    cardRadioButton.setEnabled(false);
                    anonymousRadioButton.setEnabled(false);
                    idTextField.setEditable(false);
                    cardTextField.setEditable(false);
                }
            } else if (getClientCard() != null) {
                cardTextField.setText(getClientCard().toString());
                cardRadioButton.doClick();
                if (isClientLocked()) {
                    idRadioButton.setEnabled(false);
                    cardRadioButton.setEnabled(false);
                    anonymousRadioButton.setEnabled(false);
                    idTextField.setEditable(false);
                    cardTextField.setEditable(false);
                }
            } else {
                cardRadioButton.doClick();
            }
        }
        super.setVisible(visible);
    }

    protected class CardFocusAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            cardRadioButton.doClick(0);
        }
    }

    @Override
    protected void onOkActionPerformed(ActionEvent evt) {
        Short attendanceId;
        Short selectedKeyId;
        AttendancesService attendancesService;

        try {
            attendancesService = AttendancesService.getInstance();

            isAnonymous = anonymousRadioButton.isSelected();

            /*
             * GUI garantees that there is always a key selected.
             */
            selectedKeyId = ((KeyDTO) keysComboBox.getSelectedItem()).getId();

            if (isAnonymous) {
                /*
                 * Anonymous attendance
                 */
                try {
                    attendanceId = attendancesService.checkInCasualClient(selectedKeyId);
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                /*
                 * Local validation.
                 */
                String cardText = cardTextField.getText().trim();
                String idText = idTextField.getText().trim();

                if (cardRadioButton.isSelected()) {
                    try {
                        clientCard = new Integer(cardText);
                    } catch (NumberFormatException ex) {
                        throw new ValidationException(MessageFormat.format(getString("Message.FieldIsNotFilledInCorrectly.withFieldName"), new Object[]{getString("Text.Card")}));
                    }
                } else {
                    try {
                        clientId = new Short(idText);
                    } catch (NumberFormatException ex) {
                        throw new ValidationException(MessageFormat.format(getString("Message.FieldIsNotFilledInCorrectly.withFieldName"), new Object[]{getString("Text.ID")}));
                    }
                }

                if (clientCard != null) {
                    clientId = ClientsService.getInstance().findByCard(clientCard);
                    if (clientId == null) {
                        throw new ValidationException(getString("Message.EnsureCardIsValid"));
                    }
                }

                /*
                 * TODO: a validation exception can also be thrown when the
                 * key's ID is invalid, which is a bug so should be wrapped into
                 * a runtime exception. However, we can not tell by design.
                 */
                attendanceId = attendancesService.checkInRegisteredClient(clientId, selectedKeyId);
            }

        } catch (BusinessException | ValidationException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(RESULT_EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setAttendanceId(attendanceId);
        setAnonymous(isAnonymous);
        if (!isAnonymous) {
            setClientCard(clientCard);
            setClientId(clientId);
        }
        setOrderDialogRequested(openOrderCheckBox.isSelected());

        super.onOkActionPerformed(evt);
    }

    private void checkBoxesActionPerformed(ActionEvent evt) {
        /*
         * The code does some magic to simplify the GUI and make it more
         * intuitive.
         */

        if (cardRadioButton.isSelected()) {
            cardTextField.setEnabled(true);
            cardTextField.requestFocusInWindow();
        } else {
            cardTextField.setEnabled(false);
            cardTextField.setText("");
        }

        if (idRadioButton.isSelected()) {
            idTextField.setEnabled(true);
            idTextField.requestFocusInWindow();
        } else {
            idTextField.setEnabled(false);
            idTextField.setText("");
        }
    }

    public Short getClientId() {
        return clientId;
    }

    public void setClientId(Short clientId) {
        this.clientId = clientId;
    }

    public Boolean isClientLocked() {
        return clientLocked;
    }

    public void setClientLocked(Boolean isClientLocked) {
        this.clientLocked = isClientLocked;
    }

    public Integer getClientCard() {
        return clientCard;
    }

    public void setClientCard(Integer clientCard) {
        this.clientCard = clientCard;
    }

    public Boolean isOrderDialogRequested() {
        return orderDialogRequested;
    }

    public void setOrderDialogRequested(Boolean orderDialogRequested) {
        this.orderDialogRequested = orderDialogRequested;
    }

    public Short getAttendanceId() {
        return attedanceId;
    }

    public void setAttendanceId(Short attedanceId) {
        this.attedanceId = attedanceId;
    }

    public Boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    /*
     * Session variables
     */
    private Short attedanceId;
    private Boolean isAnonymous;
    private Boolean orderDialogRequested;
    private Integer clientCard;
    private Short clientId;
    private Boolean clientLocked;
    /*
     * Components
     */
    private JPanel additionalActionsPanel;
    private JRadioButton anonymousRadioButton;
    private JButton cancelButton;
    private JTextField cardTextField;
    private ButtonGroup clientButtonGroup;
    private JRadioButton cardRadioButton;
    private JPanel clientDetailsPanel;
    private JRadioButton idRadioButton;
    private JTextField idTextField;
    private JLabel keyLabel;
    private JComboBox keysComboBox;
    private JButton okButton;
    private JCheckBox openOrderCheckBox;
}
