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
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.client.ContextManager;
import org.key2gym.client.util.ReportGeneratorCellRenderer;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PickReportFormatDialog extends AbstractDialog {

    /**
     * Creates new PickReportFormatDialog
     */
    public PickReportFormatDialog(Window parent, ReportDTO report) {
        super(parent, true);
	this.report = report;

        initComponents();
        buildDialog();
    }

    /**
     * Initializes the dialog's components.
     */
    private void initComponents() {

        reportFormatLabel = new JLabel(getString("Label.ReportFormat"));

        reportFormatsList = new JComboBox();
        reportFormatsList.setModel(new DefaultComboBoxModel(report.getReportGenerator().getFormats().toArray()));
        reportFormatsList.setRenderer(new ReportGeneratorCellRenderer());
        
        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());

        okButton.setPreferredSize(cancelButton.getPreferredSize());

        getRootPane().setDefaultButton(okButton);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.PickReportFormat")); // NOI18N        
    }

    /**
     * Builds the dialog by placing the components on it.
     */
    private void buildDialog() {

        setLayout(new FormLayout("4dlu, r:p, 3dlu, [200dlu, p]:g, 4dlu", "4dlu, p, 4dlu, c:p, 4dlu"));

        add(reportFormatLabel, CC.xy(2, 2));
        add(reportFormatsList, CC.xy(4, 2));

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
        reportFormat = (String) reportFormatsList.getSelectedItem();
        super.onOkActionPerformed(evt);
    }

    /**
     * Returns the selected report format.
     *
     * @return the selected report format
     */
    public String getReportFormat() {
        return reportFormat;
    }
    /*
     * Session variables
     */
    private String reportFormat;
    private ReportDTO report;
    /*
     * Components
     */
    private JButton cancelButton;
    private JComboBox reportFormatsList;
    private JLabel reportFormatLabel;
    private JButton okButton;
}

