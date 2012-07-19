/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.presentation.dialogs.RegisterClientDialog;
import census.presentation.dialogs.OpenAttendanceDialog;
import census.presentation.dialogs.EditFinancialActivityDialog;
import census.presentation.dialogs.CensusDialog;
import census.business.OrdersService;
import census.business.SessionsService;
import census.business.StorageService;
import census.business.api.BusinessException;
import census.business.api.ValidationException;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.joda.time.DateMidnight;

/**
 *
 * @author daniel
 */
public class RegisterClientAction extends CensusAction implements Observer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public RegisterClientAction() {
        if(!Beans.isDesignTime()) {
            update(null, null);
        }
        
        setText(bundle.getString("Text.Register"));
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/registerClient.png")));

    }

    /**
     * Processes an event.
     *
     * @param event the event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        RegisterClientDialog registerClientDialog;
        StorageService storageService = StorageService.getInstance();

        storageService.beginTransaction();

        try {
            registerClientDialog = new RegisterClientDialog(getFrame());
            registerClientDialog.setVisible(true);

            if (registerClientDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw registerClientDialog.getException();
            }

            if (registerClientDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            /*
             * If requested, creates and launches EditFinancialActivityDialog
             */
            if (registerClientDialog.isEditFinancialActivityDialogRequested()) {

                EditFinancialActivityDialog editFinancialActivityDialog = new EditFinancialActivityDialog(getFrame());

                try {
                    editFinancialActivityDialog.setFinancialActivityId(OrdersService.getInstance().findByClientIdAndDate(registerClientDialog.getClientId(), new DateMidnight(), true));
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }

                editFinancialActivityDialog.setVisible(true);

                if (editFinancialActivityDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                    throw editFinancialActivityDialog.getException();
                }

                if (editFinancialActivityDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                    storageService.rollbackTransaction();
                    return;
                }
            }

            /*
             * If requested, creates and lanches OpenAttendanceDialog
             */
            if (registerClientDialog.isOpenAttendanceDialogRequested()) {
                OpenAttendanceDialog openAttendanceDialog = new OpenAttendanceDialog(getFrame());

                openAttendanceDialog.setClientId(registerClientDialog.getClientId());
                openAttendanceDialog.setClientLocked(true);
                openAttendanceDialog.setVisible(true);

                if (openAttendanceDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                    throw openAttendanceDialog.getException();
                }

                if (openAttendanceDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                    storageService.rollbackTransaction();
                    return;
                }
            }

            storageService.commitTransaction();

        } catch (BusinessException ex) {
            storageService.rollbackTransaction();
            JOptionPane.showMessageDialog(getFrame(), ex.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        } catch (RuntimeException ex) {
            Logger.getLogger(RegisterClientAction.class.getName()).log(Level.SEVERE, null, ex);
            storageService.rollbackTransaction();
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
