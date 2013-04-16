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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.services.FreezesService;
import org.key2gym.client.ContextManager;
import org.key2gym.client.UserExceptionHandler;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;


/**
 *
 * @author Danylo Vashchilenko
 */
public class FreezeClientDialog extends AbstractDialog {
    
    /**
     * Creates new form FreezeClientDialog
     */
    public FreezeClientDialog(JFrame parent) {
        super(parent, true);
        freezesService = ContextManager.lookup(FreezesService.class);
        
        buildDialog();
        
    }

                       
    private void buildDialog() {
        setLayout(new FormLayout("4dlu, r:d, 3dlu, f:d:g, 4dlu", 
                "4dlu, d, 3dlu, f:d:g, 4dlu, d, 4dlu"));

        daysSpinner = new JSpinner();
        daysSpinner.setModel(new SpinnerNumberModel(1, 1, 10, 1));
        add(new JLabel(getString("Label.Days")), CC.xy(2, 2));
        add(daysSpinner, CC.xy(4, 2));
        
        noteTextArea = new JTextArea();        
        noteTextArea.setColumns(20);
        noteTextArea.setLineWrap(true);
        noteTextArea.setRows(3);
        noteTextAreaScrollPane = new JScrollPane();
        noteTextAreaScrollPane.setViewportView(noteTextArea);
        JLabel label = new JLabel(getString("Label.Note"));
        label.setVerticalAlignment(SwingConstants.TOP);
        add(label, CC.xy(2, 4));
        add(noteTextAreaScrollPane, CC.xy(4, 4));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, CC.xywh(2, 6, 3, 1));
        
        getRootPane().setDefaultButton(okButton);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.FreezeClient")); // NOI18N
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(getParent());
    }                      

    @Override
    protected void onOkActionPerformed(ActionEvent evt) {                                         
        Integer days = (Integer)daysSpinner.getValue();
        String note = noteTextArea.getText();
        
        try {
            freezesService.addFreeze(clientId, days, note);
        } catch (ValidationException | BusinessException | SecurityViolationException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        }

        super.onOkActionPerformed(evt);
    }                                        
                                      

    public void setClientId(Integer id) {
        this.clientId = id;
    }
    
    /*
     * Session variables
     */
    private Integer clientId;
    
    /*
     * Business services
     */
    private FreezesService freezesService;
                  
    private JButton cancelButton;
    private JSpinner daysSpinner;
    private JTextArea noteTextArea;
    private JScrollPane noteTextAreaScrollPane;
    private JButton okButton;             
}
