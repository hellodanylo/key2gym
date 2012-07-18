/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.business.FinancialActivitiesService;
import census.business.SessionsService;
import census.business.StorageService;
import census.business.api.ValidationException;
import census.presentation.CensusFrame;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.EditFinancialActivityDialog;
import census.presentation.dialogs.PickClientDialog;
import census.presentation.dialogs.PickFinancialActivityDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
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
public class EditFinancialActivityAction extends CensusAction {
    
    private ResourceBundle bundle;

    public EditFinancialActivityAction() {
        if (!Beans.isDesignTime()) {
            update(null, null);
        }
    
        bundle  = ResourceBundle.getBundle("census/presentation/resources/Strings");
        setText(bundle.getString("Text.FinancialActivity"));
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/financialActivity.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {

            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            Short financialActivityId;
            
            if(e.getActionCommand().equals(ACTION_CONTEXT)) {
                financialActivityId = CensusFrame.getInstance()
                        .getSelectedFinancialActivity().getId();
            } else {
                PickFinancialActivityDialog pickFinancialActivityDialog = new PickFinancialActivityDialog(getFrame());
                pickFinancialActivityDialog.setVisible(true);

                if (pickFinancialActivityDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                    throw pickFinancialActivityDialog.getException();
                }

                if (pickFinancialActivityDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                    storageService.rollbackTransaction();
                    return;
                }

                if (pickFinancialActivityDialog.isClient()) {
                    PickClientDialog pickClientDialog = new PickClientDialog(getFrame());
                    pickClientDialog.setVisible(true);

                    if (pickClientDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                        throw pickClientDialog.getException();
                    }

                    if (pickClientDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                        storageService.rollbackTransaction();
                        return;
                    }

                    try {
                        financialActivityId = FinancialActivitiesService.getInstance().findByClientIdAndDate(pickClientDialog.getClientId(), new DateMidnight(), true);
                    } catch (ValidationException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    financialActivityId = pickFinancialActivityDialog.getFinancialActivityId();
                }
            
            }

            EditFinancialActivityDialog editFinancialActivityDialog = new EditFinancialActivityDialog(getFrame());
            editFinancialActivityDialog.setFinancialActivityId(financialActivityId);
            editFinancialActivityDialog.setFullPaymentForced(false);
            editFinancialActivityDialog.setVisible(true);

            if (editFinancialActivityDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw editFinancialActivityDialog.getException();
            }

            if (editFinancialActivityDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
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
        Boolean open = SessionsService.getInstance().hasOpenSession();
        setEnabled(open);
    }

}
