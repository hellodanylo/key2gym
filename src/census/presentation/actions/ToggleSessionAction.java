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
import census.business.StorageService;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.OpenSessionDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ToggleSessionAction extends CensusAction implements Observer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");
    
    public ToggleSessionAction() {
        if(!Beans.isDesignTime()) {
            update(null, null);
        } else {
            setText(bundle.getString("Text.OpenSession"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            StorageService.getInstance().beginTransaction();
            
            if(SessionsService.getInstance().hasOpenSession()) {
                SessionsService.getInstance().closeSession();
            } else {
                OpenSessionDialog openSessionDialog = new OpenSessionDialog(getFrame());
                openSessionDialog.setVisible(true);

                if(openSessionDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                    StorageService.getInstance().rollbackTransaction();
                    return;
                }

                if(openSessionDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                    throw openSessionDialog.getException();
                }
            }
            
            StorageService.getInstance().commitTransaction();
            
        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if(StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
        }
    }

    @Override
    public final void update(Observable o, Object arg) {
        if(o == null) {
            SessionsService.getInstance().addObserver(this);
        }
        setText(SessionsService.getInstance().hasOpenSession() ? bundle.getString("Text.CloseSession") : bundle.getString("Text.OpenSession"));
    }

}
