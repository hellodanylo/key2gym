/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.presentation.actions;

import census.business.SessionsService;
import census.business.StorageService;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.RaiseAdministratorDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

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
                RaiseAdministratorDialog raisePermissionsLevelDialog = new RaiseAdministratorDialog(getFrame());
                raisePermissionsLevelDialog.setVisible(true);

                if(raisePermissionsLevelDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                    StorageService.getInstance().rollbackTransaction();
                    return;
                }

                if(raisePermissionsLevelDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                    throw raisePermissionsLevelDialog.getException();
                }
            }
            
            StorageService.getInstance().commitTransaction();
            
        } catch (RuntimeException ex) {
            Logger.getLogger(RegisterClientAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            StorageService.getInstance().rollbackTransaction();
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
