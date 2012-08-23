/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.business.OrdersService;
import census.business.StorageService;
import census.business.api.ValidationException;
import census.presentation.MainFrame;
import census.presentation.dialogs.AbstractDialog;
import census.presentation.dialogs.EditOrderDialog;
import census.presentation.dialogs.PickClientDialog;
import census.presentation.dialogs.PickOrderDialog;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class EditOrderAction extends BasicAction {

    public EditOrderAction() {
        
        setText(getString("Text.Orders"));
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/order.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {

            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            Short orderId;

            if (e.getActionCommand().equals(ACTION_CONTEXT)) {
                orderId = MainFrame.getInstance().getSelectedOrder().getId();
            } else {
                PickOrderDialog pickOrderDialog = new PickOrderDialog(getFrame());
                pickOrderDialog.setVisible(true);

                if (pickOrderDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                    throw pickOrderDialog.getException();
                }

                if (pickOrderDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                    storageService.rollbackTransaction();
                    return;
                }

                if (pickOrderDialog.isClient()) {
                    PickClientDialog pickClientDialog = new PickClientDialog(getFrame());
                    pickClientDialog.setVisible(true);

                    if (pickClientDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                        throw pickClientDialog.getException();
                    }

                    if (pickClientDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                        storageService.rollbackTransaction();
                        return;
                    }

                    try {
                        orderId = OrdersService.getInstance().findByClientIdAndDate(pickClientDialog.getClientId(), new DateMidnight(), true);
                    } catch (ValidationException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    orderId = pickOrderDialog.getOrderId();
                }

            }

            EditOrderDialog editOrderDialog = new EditOrderDialog(getFrame());
            editOrderDialog.setOrderId(orderId);
            editOrderDialog.setFullPaymentForced(false);
            editOrderDialog.setVisible(true);

            if (editOrderDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw editOrderDialog.getException();
            }

            if (editOrderDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            storageService.commitTransaction();

        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), getString("Message.ProgramEncounteredError"), getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if (StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
        }
    }
}
