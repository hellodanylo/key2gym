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

import org.key2gym.business.AttendancesService;
import org.key2gym.business.OrdersService;
import org.key2gym.business.StorageService;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.AttendanceDTO;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.EditOrderDialog;
import org.key2gym.presentation.dialogs.PickAttendanceDialog;
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
public class CheckOutAction extends BasicAction {

    public CheckOutAction() {        
        setText(getString("Text.Leaving"));
        setIcon(new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/close.png")));
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        /*
         * Business
         */
        StorageService storageService = null;
        
        /*
         * Presentation
         */
        PickAttendanceDialog pickAttendanceDialog;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();
            
            pickAttendanceDialog = new PickAttendanceDialog(getFrame());
            
            AttendanceDTO attendanceDTO = getFrame().getSelectedAttendance();
            if (attendanceDTO != null) {
                pickAttendanceDialog.setAttendanceId(attendanceDTO.getId());
                pickAttendanceDialog.setAttendanceLocked(false);
            }

            pickAttendanceDialog.setVisible(true);

            if (pickAttendanceDialog.getResult().equals(AbstractDialog.Result.EXCEPTION)) {
                throw pickAttendanceDialog.getException();
            }

            if (pickAttendanceDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }
            
            Integer attendanceId = pickAttendanceDialog.getAttendanceId();
            
            if(pickAttendanceDialog.isEditOrderDialogRequested()) {
                Integer orderId;
                Boolean isAnonymous;

                try {
                    isAnonymous = AttendancesService.getInstance().isCasual(attendanceId);
                    if(isAnonymous) {
                        orderId = OrdersService.getInstance().findForAttendanceById(attendanceId);
                    } else {
                        orderId = OrdersService.getInstance().findByClientIdAndDate(AttendancesService.getInstance().getAttendanceById(attendanceId).getClientId(), new DateMidnight(), true);
                    }
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }

                if(orderId != null) {

                    EditOrderDialog editOrderDialog = new EditOrderDialog(getFrame());
                    editOrderDialog.setOrderId(orderId);
                    editOrderDialog.setFullPaymentForced(isAnonymous);
                    editOrderDialog.setVisible(true);

                    if (editOrderDialog.getResult().equals(AbstractDialog.Result.EXCEPTION)) {
                        throw editOrderDialog.getException();
                    }

                    if (editOrderDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
                        storageService.rollbackTransaction();
                        return;
                    }
                }
            }
            
            /*
             * Finally, closes the attendance.
             */
            try {
                AttendancesService.getInstance().checkOut(attendanceId);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }
            
            storageService.commitTransaction();

        } catch (BusinessException ex) {
            storageService.rollbackTransaction();
            UserExceptionHandler.getInstance().processException(ex);
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
