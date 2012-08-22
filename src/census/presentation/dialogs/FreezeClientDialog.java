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

import census.business.FreezesService;
import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.presentation.util.UserExceptionHandler;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;


/**
 *
 * @author Danylo Vashchilenko
 */
public class FreezeClientDialog extends CensusDialog {
    
    /**
     * Creates new form FreezeClientDialog
     */
    public FreezeClientDialog(JFrame parent) {
        super(parent, true);
        freezesService = FreezesService.getInstance();
        
        buildDialog();
        
    }

                       
    private void buildDialog() {
        setLayout(new FormLayout("4dlu, r:d, 3dlu, f:d:g, 4dlu", 
                "4dlu, d, 3dlu, f:d:g, 4dlu, d, 4dlu"));

        daysSpinner = new JSpinner();
        daysSpinner.setModel(new SpinnerNumberModel(Short.valueOf((short)1), Short.valueOf((short)1), Short.valueOf((short)10), Short.valueOf((short)1)));
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
        Short days = (Short) daysSpinner.getValue();
        String note = noteTextArea.getText();
        
        try {
            freezesService.addFreeze(clientId, days, note);
        } catch (ValidationException | BusinessException | SecurityException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        }

        super.onOkActionPerformed(evt);
    }                                        
                                      

    public void setClientId(Short id) {
        this.clientId = id;
    }
    
    /*
     * Session variables
     */
    private Short clientId;
    
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
