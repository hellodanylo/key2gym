/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.business.SessionsService;
import census.business.StorageService;
import census.presentation.dialogs.PickClientDialog;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.EditClientDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Danylo Vashchilenko
 */
public class EditClientAction extends CensusAction implements Observer {

    private ResourceBundle bundle;

    public EditClientAction() {
        if (!Beans.isDesignTime()) {
            update(null, null);
        }
        bundle  = ResourceBundle.getBundle("census/presentation/resources/Strings");
        setText(bundle.getString("Text.Edit"));
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/edit64.png")));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            final PickClientDialog pickClientDialog = new PickClientDialog(getFrame());
            pickClientDialog.setVisible(true);

            if (pickClientDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw pickClientDialog.getException();
            }

            if (pickClientDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            final EditClientDialog editClientDialog = new EditClientDialog(getFrame());
            new Thread() {

                @Override
                public void run() {
                    editClientDialog.setClientId(pickClientDialog.getClientId());
                }
            }.start();
            editClientDialog.setVisible(true);

            if (editClientDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw editClientDialog.getException();
            }

            if (editClientDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            storageService.commitTransaction();

        } catch (RuntimeException ex) {
            Logger.getLogger(RegisterClientAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if (storageService != null) {
                storageService.rollbackTransaction();
            }
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
