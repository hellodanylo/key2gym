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

import census.business.SessionsService;
import census.business.api.SecurityException;
import census.presentation.MainFrame;
import census.presentation.dialogs.AbstractDialog;
import census.presentation.dialogs.PickDateDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OpenItemsWindowAction extends CensusAction {

    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public OpenItemsWindowAction() {
        if (!Beans.isDesignTime()) {
            update(null, null);
        }
        
        setAccelerationKey(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
        setText(bundle.getString("Text.Items"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            MainFrame.getInstance().openItemsTab();
        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    @Override
    public final void update(Observable o, Object arg) {
        if (o == null) {
            SessionsService.getInstance().addObserver(this);
        }
        Boolean open = SessionsService.getInstance().hasOpenSession();
        setEnabled(open);
    }
}
