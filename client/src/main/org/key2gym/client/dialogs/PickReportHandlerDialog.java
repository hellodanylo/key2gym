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
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.util.*;
import org.key2gym.business.api.UserException;
import org.key2gym.client.report.spi.ReportHandler;
import org.key2gym.client.ContextManager;
import org.key2gym.client.util.ReportHandlerCellRenderer;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PickReportHandlerDialog extends AbstractDialog {

    /**
     * Creates new PickReportHandlerDialog
     */
    public PickReportHandlerDialog(Window parent, String reportFormat) throws UserException {
        super(parent, true);
	this.reportFormat = reportFormat;

    	Iterator<ReportHandler> iterator = ServiceLoader.load(ReportHandler.class).iterator();
	handlers = new LinkedList<ReportHandler>();
	
        while(iterator.hasNext()) {
            ReportHandler handler = iterator.next();
            if(handler.isSupported(reportFormat)) {
                handlers.add(handler);
            }
        }
	
        if(handlers.isEmpty()) {
            throw new UserException(getString("Message.ReportsTypeNotSupported"));
        }

        initComponents();
        buildDialog();
    }

    /**
     * Initializes the dialog's components.
     */
    private void initComponents() {

        reportHandlerLabel = new JLabel(getString("Label.ReportHandler"));

        reportHandlersList = new JComboBox();
        reportHandlersList.setModel(new DefaultComboBoxModel(handlers.toArray()));
        reportHandlersList.setRenderer(new ReportHandlerCellRenderer());
        
        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());

        okButton.setPreferredSize(cancelButton.getPreferredSize());

        getRootPane().setDefaultButton(okButton);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.PickReportHandler")); // NOI18N        
    }

    /**
     * Builds the dialog by placing the components on it.
     */
    private void buildDialog() {

        setLayout(new FormLayout("4dlu, r:p, 3dlu, [200dlu, p]:g, 4dlu", "4dlu, p, 4dlu, c:p, 4dlu"));

        add(reportHandlerLabel, CC.xy(2, 2));
        add(reportHandlersList, CC.xy(4, 2));

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
        handler = (ReportHandler) reportHandlersList.getSelectedItem();
        super.onOkActionPerformed(evt);
    }

    /**
     * Returns the selected report handler.
     *
     * @return the selected report handler
     */
    public ReportHandler getReportHandler() {
        return handler;
    }

    /*
     * Session variables
     */
    private String reportFormat;
    private List<ReportHandler> handlers;
    private ReportHandler handler;
    /*
     * Components
     */
    private JButton cancelButton;
    private JComboBox reportHandlersList;
    private JLabel reportHandlerLabel;
    private JButton okButton;
}

