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

import census.business.AttendancesService;
import census.business.OrdersService;
import census.business.StorageService;
import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.AttendanceDTO;
import census.presentation.dialogs.AbstractDialog;
import census.presentation.dialogs.EditOrderDialog;
import census.presentation.dialogs.PickAttendanceDialog;
import census.presentation.util.UserExceptionHandler;
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
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/close.png")));
        
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

            if (pickAttendanceDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw pickAttendanceDialog.getException();
            }

            if (pickAttendanceDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }
            
            Short attendanceId = pickAttendanceDialog.getAttendanceId();
            
            if(pickAttendanceDialog.isEditOrderDialogRequested()) {
                Short financialActivityId;
                Boolean isAnonymous;

                try {
                    isAnonymous = AttendancesService.getInstance().isCasual(attendanceId);
                    if(isAnonymous) {
                        financialActivityId = OrdersService.getInstance().findForAttendanceById(attendanceId);
                    } else {
                        financialActivityId = OrdersService.getInstance().findByClientIdAndDate(AttendancesService.getInstance().getAttendanceById(attendanceId).getClientId(), new DateMidnight(), true);
                    }
                } catch (ValidationException | SecurityException ex) {
                    throw new RuntimeException(ex);
                }

                if(financialActivityId != null) {

                    EditOrderDialog editOrderDialog = new EditOrderDialog(getFrame());
                    editOrderDialog.setOrderId(financialActivityId);
                    editOrderDialog.setFullPaymentForced(isAnonymous);
                    editOrderDialog.setVisible(true);

                    if (editOrderDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                        throw editOrderDialog.getException();
                    }

                    if (editOrderDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
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
