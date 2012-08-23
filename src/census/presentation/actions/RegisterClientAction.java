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

import census.presentation.dialogs.RegisterClientDialog;
import census.presentation.dialogs.CheckInDialog;
import census.presentation.dialogs.EditOrderDialog;
import census.presentation.dialogs.AbstractDialog;
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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class RegisterClientAction extends BasicAction {

    public RegisterClientAction() {
        setText(getString("Text.Register"));
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

            if (registerClientDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw registerClientDialog.getException();
            }

            if (registerClientDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            /*
             * If requested, creates and launches EditOrderDialog
             */
            if (registerClientDialog.isEditFinancialActivityDialogRequested()) {

                EditOrderDialog editFinancialActivityDialog = new EditOrderDialog(getFrame());

                try {
                    editFinancialActivityDialog.setOrderId(OrdersService.getInstance().findByClientIdAndDate(registerClientDialog.getClientId(), new DateMidnight(), true));
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }

                editFinancialActivityDialog.setVisible(true);

                if (editFinancialActivityDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                    throw editFinancialActivityDialog.getException();
                }

                if (editFinancialActivityDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                    storageService.rollbackTransaction();
                    return;
                }
            }

            /*
             * If requested, creates and lanches CheckInDialog
             */
            if (registerClientDialog.isOpenAttendanceDialogRequested()) {
                CheckInDialog openAttendanceDialog = new CheckInDialog(getFrame());

                openAttendanceDialog.setClientId(registerClientDialog.getClientId());
                openAttendanceDialog.setVisible(true);

                if (openAttendanceDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                    throw openAttendanceDialog.getException();
                }

                if (openAttendanceDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
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
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), getString("Message.ProgramEncounteredError"), getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if(StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
        }
    }
}
