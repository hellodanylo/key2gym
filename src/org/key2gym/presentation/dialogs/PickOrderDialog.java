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

import org.key2gym.business.AttendancesService;
import org.key2gym.business.KeysService;
import org.key2gym.business.OrdersService;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.AttendanceDTO;
import org.key2gym.business.dto.KeyDTO;
import org.key2gym.presentation.renderers.KeyListCellRenderer;
import org.key2gym.presentation.util.UserExceptionHandler;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PickOrderDialog extends AbstractDialog {

    /**
     * Creates new PickOrderDialog
     *
     * @param parent the frame the dialog should align with
     * @param session the session used by the dialog and its caller
     */
    public PickOrderDialog(JFrame parent) {
        super(parent, true);

        initComponents();
        buildDialog();
    }

    /**
     * Initializes the dialog's components.
     */
    private void initComponents() {

        modeButtonGroup = new ButtonGroup();

        clientRadioButton = new JRadioButton();
        clientRadioButton.setText(getString("Text.Client")); // NOI18N
        clientRadioButton.setActionCommand("clientCard");
        clientRadioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                radioButtonsActionPerformed(evt);
            }
        });
        modeButtonGroup.add(clientRadioButton);

        /*
         * Key
         */
        keyRadioButton = new JRadioButton();
        keyRadioButton.setText(getString("Label.Key")); // NOI18N
        keyRadioButton.setActionCommand("attendance");
        keyRadioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                radioButtonsActionPerformed(evt);
            }
        });
        modeButtonGroup.add(keyRadioButton);

        keysComboBox = new JComboBox();
        keysComboBox.setRenderer(new KeyListCellRenderer());

        List<KeyDTO> keys = KeysService.getInstance().getKeysTaken();
        if (keys.isEmpty()) {
            keyRadioButton.setEnabled(false);
            clientRadioButton.doClick();
        } else {
            keysComboBox.setModel(new DefaultComboBoxModel(keys.toArray()));
            keyRadioButton.doClick();
        }

        /*
         * Other
         */
        otherRadioButton = new JRadioButton();
        otherRadioButton.setText(getString("Text.Other")); // NOI18N
        otherRadioButton.setActionCommand("other");
        otherRadioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                radioButtonsActionPerformed(evt);
            }
        });
        modeButtonGroup.add(otherRadioButton);

        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());

        okButton.setPreferredSize(cancelButton.getPreferredSize());
        getRootPane().setDefaultButton(okButton);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.PickOrder")); // NOI18N  
    }

    /**
     * Builds the dialog by placing the components on it.
     */
    private void buildDialog() {

        setLayout(new FormLayout("4dlu, [150dlu, p], 4dlu", "4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu"));

        add(clientRadioButton, CC.xy(2, 2));
        add(keyRadioButton, CC.xy(2, 4));
        add(keysComboBox, CC.xy(2, 6));
        add(otherRadioButton, CC.xy(2, 8));

        JPanel buttonsPanel = new JPanel();
        {
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            buttonsPanel.add(okButton);
            buttonsPanel.add(cancelButton);
        }
        add(buttonsPanel, CC.xy(2, 10));

        setResizable(false);
        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Process a radio button event.
     *
     * @param evt the action event, optional
     */
    private void radioButtonsActionPerformed(ActionEvent evt) {
        /*
         * The code does some magic to simplify the GUI and make it more
         * intuitive.
         */

        if (keyRadioButton.isSelected()) {
            keysComboBox.setEnabled(true);
            keysComboBox.requestFocusInWindow();
        } else {
            keysComboBox.setEnabled(false);
        }
    }

    /**
     * Processes an OK button click.
     *
     * @param evt the action event, optional
     */
    @Override
    protected void onOkActionPerformed(ActionEvent evt) {
        /*
         * The API requires just to return a valid financial activity's ID upon
         * a sucessful completion.
         */
        try {

            if (clientRadioButton.isSelected()) {
                setResult(Result.OK);
                setClient(true);
                dispose();
                return;

                /*
                 * The attendance's key is provided.
                 */
            } else if (keyRadioButton.isSelected()) {
                /*
                 * GUI garantees that there is a key selected, if the
                 * attendanceRadionButton is selected
                 */
                KeyDTO key = (KeyDTO) keysComboBox.getSelectedItem();
                try {
                    AttendancesService attendancesService = AttendancesService.getInstance();
                    OrdersService financialActivitiesService = OrdersService.getInstance();

                    Integer attendanceId = attendancesService.findOpenAttendanceByKey(key.getId());
                    AttendanceDTO attendanceDTO = attendancesService.getAttendanceById(attendanceId);
                    if (attendanceDTO.getClientId() == null) {
                        /*
                         * The API requires to pass only anonymous attendances
                         * to findForAttendanceById
                         */
                        orderId = financialActivitiesService.findForAttendanceById(attendanceId);
                    } else {
                        orderId = financialActivitiesService.findByClientIdAndDate(attendanceDTO.getClientId(), new DateMidnight(), true);
                    }
                    /*
                     * All exceptions are unexpected, and, therefore, are bugs.
                     */
                } catch (BusinessException | ValidationException | SecurityException ex) {
                    throw new RuntimeException(ex);
                }
                /*
                 * A default financial activity is needed.
                 */
            } else {
                orderId = OrdersService.getInstance().findCurrentDefault(true);
            }
        } catch (ValidationException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(Result.EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setClient(false);
        setOrderId(orderId);

        super.onOkActionPerformed(evt);
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer financialActivityId) {
        this.orderId = financialActivityId;
    }

    public Boolean isClient() {
        return client;
    }

    public void setClient(Boolean client) {
        this.client = client;
    }
    /*
     * Session variables
     */
    private Integer orderId;
    private Boolean client;
    /*
     * Components
     */
    private JRadioButton keyRadioButton;
    private JComboBox keysComboBox;
    private JButton cancelButton;
    private JRadioButton clientRadioButton;
    private ButtonGroup modeButtonGroup;
    private JButton okButton;
    private JRadioButton otherRadioButton;
}
