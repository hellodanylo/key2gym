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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AboutDialog extends AbstractDialog {

    /**
     * Creates new form AboutDialog
     */
    public AboutDialog(JFrame parent) {
        super(parent, true);
        buildDialog();
    }

     private void buildDialog() {
        setLayout(new FormLayout("4dlu, f:d, 4dlu", "4dlu, d, 4dlu, d, 4dlu, d, 4dlu"));
        
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/splash.png"))); // NOI18N
        add(logoLabel, CC.xy(2, 2));
       
        JEditorPane informationEditorPane = new JEditorPane();
        informationEditorPane.setContentType("text/html");
        informationEditorPane.setEditable(false);
        informationEditorPane.setText(getString("Html.About")); // NOI18N
        informationEditorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                informationEditorPaneHyperlinkUpdate(evt);
            }
        });
        
        JScrollPane informationScrollPane = new JScrollPane();
        informationScrollPane.setViewportView(informationEditorPane);
        add(informationScrollPane, CC.xy(2, 4));
        
        add(new JButton(getOkAction()), CC.xy(2, 6));
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.About")); // NOI18N
        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
    }
     
    private void informationEditorPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_informationEditorPaneHyperlinkUpdate
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(evt.getURL().toURI());
                } catch (IOException|URISyntaxException ex) {
                    Logger.getLogger(AboutDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
