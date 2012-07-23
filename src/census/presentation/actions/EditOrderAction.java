/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.business.OrdersService;
import census.business.SessionsService;
import census.business.StorageService;
import census.business.api.ValidationException;
import census.presentation.CensusFrame;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.EditOrderDialog;
import census.presentation.dialogs.PickClientDialog;
import census.presentation.dialogs.PickOrderDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

/**
 *
 * @author daniel
 */
public class EditOrderAction extends CensusAction {
    
    private ResourceBundle bundle;

    public EditOrderAction() {
        if (!Beans.isDesignTime()) {
            update(null, null);
        }
    
        bundle  = ResourceBundle.getBundle("census/presentation/resources/Strings");
        setText(bundle.getString("Text.Orders"));
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/order.png")));
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
                        .getSelectedOrder().getId();
            } else {
                PickOrderDialog pickFinancialActivityDialog = new PickOrderDialog(getFrame());
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
                        financialActivityId = OrdersService.getInstance().findByClientIdAndDate(pickClientDialog.getClientId(), new DateMidnight(), true);
                    } catch (ValidationException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    financialActivityId = pickFinancialActivityDialog.getFinancialActivityId();
                }
            
            }

            EditOrderDialog editFinancialActivityDialog = new EditOrderDialog(getFrame());
            editFinancialActivityDialog.setOrderId(financialActivityId);
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
        Boolean open = SessionsService.getInstance().hasOpenSession();
        setEnabled(open);
    }

}
