/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.presentation.actions;

import census.business.SessionsService;
import census.business.StorageService;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.ManageCashDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageCashAction extends CensusAction implements Observer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

     public ManageCashAction() {
        if(!Beans.isDesignTime()) {
            update(null, null);
        }
        
        setText(bundle.getString("Text.Cash"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            ManageCashDialog manageCashDialog = new ManageCashDialog(getFrame());
            manageCashDialog.setDate(new DateMidnight());
            manageCashDialog.setVisible(true);

            if (manageCashDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw manageCashDialog.getException();
            }

            if (manageCashDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            storageService.commitTransaction();

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
        if (o == null) {
            SessionsService.getInstance().addObserver(this);
        }
        Boolean hasSessionAndAllPermissions = SessionsService.getInstance().hasOpenSession() &&
                SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL);
        setEnabled(hasSessionAndAllPermissions);
    }

}
