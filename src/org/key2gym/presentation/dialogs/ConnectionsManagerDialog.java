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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import org.key2gym.persistence.connections.configurations.ConnectionConfiguration;
import org.key2gym.persistence.connections.ConnectionConfigurationsManager;
import org.key2gym.presentation.renderers.ConnectionsListCellRenderer;
import org.key2gym.presentation.util.MutableListModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ConnectionsManagerDialog extends AbstractDialog {

    public ConnectionsManagerDialog(ConnectionConfigurationsManager connectionsManager) {
        super(null, true);
        this.connectionsManager = connectionsManager;

        buildDialog();
    }

    private void buildDialog() {
        setLayout(new FormLayout("4dlu, d:g, 4dlu",
                "4dlu, d, 3dlu, f:d:g, 4dlu, d, 4dlu"));
        setTitle(getString("Title.ConnectionsManager"));

        add(new JLabel(new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/logo-wide.png"))), CC.xy(2, 2));

        JPanel connectionsPanel = new JPanel(new BorderLayout());
        connectionsPanel.setBorder(BorderFactory.createTitledBorder(getString("Text.Connections")));

        connectionsList = new JList();

        connectionsListModel = new MutableListModel();
        List<ConnectionConfiguration> connections = connectionsManager.getConnections();
        connectionsListModel.set(connections);
        connectionsList.setModel(connectionsListModel);
        if (connections.size() > 0) {
            connectionsList.setSelectedIndex(0);
        }

        connectionsList.setCellRenderer(new ConnectionsListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(connectionsList);
        connectionsPanel.add(scrollPane, BorderLayout.CENTER);

        add(connectionsPanel, CC.xy(2, 4));
        add(createButtonsPanel(), CC.xy(2, 6));

        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton okButton = new JButton(getOkAction());
        JButton cancelButton = new JButton(getCancelAction());
        okButton.setPreferredSize(cancelButton.getPreferredSize());

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }
    
    @Override
    protected void onOkActionPerformed(ActionEvent evt) {
        connectionsManager.selectConnection(connectionsList.getSelectedValue());
        super.onOkActionPerformed(evt);
    }
    /*
     * Misc
     */
    private ConnectionConfigurationsManager connectionsManager;
    private MutableListModel connectionsListModel;
    /*
     * Components
     */
    private JList<ConnectionConfiguration> connectionsList;
}
