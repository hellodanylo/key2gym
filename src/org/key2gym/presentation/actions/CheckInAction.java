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
package org.key2gym.presentation.actions;

import org.key2gym.business.OrdersService;
import org.key2gym.business.StorageService;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.CheckInDialog;
import org.key2gym.presentation.dialogs.EditOrderDialog;
import org.key2gym.presentation.util.UserExceptionHandler;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public final class CheckInAction extends BasicAction {

    private Logger logger = Logger.getLogger(CheckInAction.class.getName());

    public CheckInAction() {
        setText(getString("Text.Entry"));
        setIcon(new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/open.png")));
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            StorageService storageService = StorageService.getInstance();

            storageService.beginTransaction();

            /*
             * OpenAttendance
             */
            CheckInDialog checkInDialog = new CheckInDialog(getFrame());
            checkInDialog.setVisible(true);

            if (checkInDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw checkInDialog.getException();
            }

            if (checkInDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            /*
             * If requested, EditFinancialActivty
             */
            if (checkInDialog.isOrderRequested()) {

                Short orderId = null;
                EditOrderDialog editOrderDialog = new EditOrderDialog(getFrame());

                try {
                    if (checkInDialog.isAnonymous()) {
                        orderId = OrdersService.getInstance().findForAttendanceById(checkInDialog.getAttendanceId());
                    } else {
                        orderId = OrdersService.getInstance().findByClientIdAndDate(checkInDialog.getClientId(), new DateMidnight(), true);
                    }
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }

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
            }

            storageService.commitTransaction();

        } catch (BusinessException ex) {
            StorageService.getInstance().rollbackTransaction();
            UserExceptionHandler.getInstance().processException(ex);
            return;
        } catch (RuntimeException ex) {
            logger.error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), getString("Message.ProgramEncounteredError"), getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if (StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
        }

    }
}
