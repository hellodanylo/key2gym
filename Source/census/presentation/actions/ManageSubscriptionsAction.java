/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.business.SessionsService;
import census.business.StorageService;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.ManageSubscriptionsDialog;
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
public class ManageSubscriptionsAction extends CensusAction implements Observer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public ManageSubscriptionsAction() {
        if(!Beans.isDesignTime()) {
            update(null, null);
        }
        
        setText(bundle.getString("Text.Subscriptions"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            ManageSubscriptionsDialog manageSubscriptionsDialog = new ManageSubscriptionsDialog(getFrame());
            manageSubscriptionsDialog.setVisible(true);

            if (manageSubscriptionsDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw manageSubscriptionsDialog.getException();
            }

            if (manageSubscriptionsDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            storageService.commitTransaction();

        } catch (RuntimeException ex) {
            Logger.getLogger(RegisterClientAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            storageService.rollbackTransaction();
            return;
        }
    }

    @Override
    public final void update(Observable o, Object arg) {
        if (o == null) {
            SessionsService.getInstance().addObserver(this);
        }
        Boolean hasSessionAndAllPermissions = SessionsService.getInstance().hasOpenSession() &&
                SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL);
        setEnabled(hasSessionAndAllPermissions);
    }
}
