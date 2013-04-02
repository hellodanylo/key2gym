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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.ValidationException;
import org.key2gym.client.UserExceptionHandler;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PickDateDialog extends AbstractDialog {

    /**
     * Creates new PickDateDialog
     */
    public PickDateDialog(JFrame parent) {
        super(parent, true);

        initComponents();
        buildDialog();
    }

    /**
     * Initializes the dialog's components.
     */
    private void initComponents() {

        dateLabel = new JLabel(getString("Label.Date"));

        dateComboBox = new JComboBox();
        dateComboBox.setEditable(true);
        DateMidnight startDate = new DateMidnight();
        String[] dates = new String[10];
        for (int i = 0; i < 10; i++) {
            dates[i] = startDate.toString("dd-MM-yyyy");
            startDate = startDate.minusDays(1);
        }
        dateComboBox.setModel(new DefaultComboBoxModel(dates));

        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());

        okButton.setPreferredSize(cancelButton.getPreferredSize());

        getRootPane().setDefaultButton(okButton);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.PickDate")); // NOI18N        
    }

    /**
     * Builds the dialog by placing the components on it.
     */
    private void buildDialog() {

        setLayout(new FormLayout("4dlu, r:p, 3dlu, p:g, 4dlu", "4dlu, p, 4dlu, c:p, 4dlu"));

        add(dateLabel, CC.xy(2, 2));
        add(dateComboBox, CC.xy(4, 2));

        JPanel buttonsPanel = new JPanel();
        {
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            buttonsPanel.add(okButton);
            buttonsPanel.add(cancelButton);
        }
        add(buttonsPanel, CC.xywh(2, 4, 3, 1));

        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    @Override
    protected void onOkActionPerformed(ActionEvent evt) {
        String value = (String) dateComboBox.getSelectedItem();

        try {
            date = new DateMidnight(new SimpleDateFormat("dd-MM-yyyy").parse(value));
        } catch (ParseException ex) {
            UserExceptionHandler.getInstance().processException(new ValidationException(getString("Message.DateInvalid")));
            return;
        }

        super.onOkActionPerformed(evt);
    }

    /**
     * Returns the selected date.
     *
     * @return the selected date.
     */
    public DateMidnight getDate() {
        return date;
    }
    /*
     * Session variables
     */
    private DateMidnight date;
    /*
     * Components
     */
    private JButton cancelButton;
    private JComboBox dateComboBox;
    private JLabel dateLabel;
    private JButton okButton;
}
