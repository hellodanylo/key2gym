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
package org.key2gym.client.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CloseableTabPanel extends javax.swing.JPanel {

    /**
     * Creates new CloseableTabPanel
     */
    public CloseableTabPanel(String text) {
        this.text = text;

        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setOpaque(false);
        
        add(new JLabel(text));

        closeButton = new JButton("x");
        add(closeButton);
    }

    public void addActionListener(ActionListener actionListener) {
        closeButton.addActionListener(actionListener);
    }

    public void removeActionListener(ActionListener actionListener) {
        closeButton.removeActionListener(actionListener);
    }
    private JButton closeButton;
    private String text;
}
