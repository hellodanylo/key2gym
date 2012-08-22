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
import census.presentation.dialogs.AbstractDialog;
import census.presentation.dialogs.TemporalySwapAdministratorDialog;
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
public class ToggleRaisedAdministratorAction extends CensusAction implements Observer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public ToggleRaisedAdministratorAction() {
        if(!Beans.isDesignTime()) {
            update(null, null);
        } else {
            setText("Raise");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            StorageService.getInstance().beginTransaction();
            
            if(SessionsService.getInstance().hasRaisedAdministrator()) {
                SessionsService.getInstance().dropRaisedAdministrator();
            } else {
                TemporalySwapAdministratorDialog swapAdminsitratorDialog = new TemporalySwapAdministratorDialog(getFrame());
                swapAdminsitratorDialog.setVisible(true);

                if(swapAdminsitratorDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                    StorageService.getInstance().rollbackTransaction();
                    return;
                }

                if(swapAdminsitratorDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                    throw swapAdminsitratorDialog.getException();
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
        
        if(SessionsService.getInstance().hasOpenSession()) {
            Boolean raised = SessionsService.getInstance().hasRaisedAdministrator();
            setText(raised ? bundle.getString("Text.Drop") : bundle.getString("Text.Raise"));    
            setEnabled(true);
        } else {
            setEnabled(false);
            setText(bundle.getString("Text.Raise"));
        }
    }

}
