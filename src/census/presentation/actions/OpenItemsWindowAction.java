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
package census.presentation.actions;

import census.presentation.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OpenItemsWindowAction extends BasicAction {

    public OpenItemsWindowAction() {       
        setAccelerationKey(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
        setText(getString("Text.Items"));
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            MainFrame.getInstance().openItemsTab();
        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), getString("Message.ProgramEncounteredError"), getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
