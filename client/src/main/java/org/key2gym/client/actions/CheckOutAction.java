/*
 * Copyright 2012-2013 Danylo Vashchilenko
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
package org.key2gym.client.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.AttendanceDTO;
import org.key2gym.business.api.services.AttendancesService;
import org.key2gym.business.api.services.OrdersService;
import org.key2gym.client.ContextManager;
import org.key2gym.client.dialogs.AbstractDialog;
import org.key2gym.client.dialogs.EditOrderDialog;
import org.key2gym.client.dialogs.PickAttendanceDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CheckOutAction extends BasicAction {

    public CheckOutAction() {
        setText(getString("Text.Leaving"));
        setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/close.png")));
    }

    @Override
    public void onActionPerformed(ActionEvent e) throws BusinessException, ValidationException, SecurityViolationException {

        AttendancesService attendancesService = ContextManager.lookup(AttendancesService.class);
        OrdersService ordersService = ContextManager.lookup(OrdersService.class);

        /*
         * Presentation
         */
        PickAttendanceDialog pickAttendanceDialog;

        pickAttendanceDialog = new PickAttendanceDialog(getFrame());

        AttendanceDTO attendanceDTO = getFrame().getSelectedAttendance();
        if (attendanceDTO != null) {
            pickAttendanceDialog.setAttendanceId(attendanceDTO.getId());
            pickAttendanceDialog.setAttendanceLocked(false);
        }

        pickAttendanceDialog.setVisible(true);

        if (pickAttendanceDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
            return;
        }

        Integer attendanceId = pickAttendanceDialog.getAttendanceId();

        if (pickAttendanceDialog.isEditOrderDialogRequested()) {
            Integer orderId;
            Boolean isCasual;

            try {
                isCasual = attendancesService.isCasual(attendanceId);
                if (isCasual) {
                    orderId = ordersService.findForAttendanceById(attendanceId);
                } else {
                    orderId = ordersService.findByClientIdAndDate(attendancesService.getAttendanceById(attendanceId).getClientId(), new DateMidnight(), true);
                }
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }

            if (orderId != null) {

                EditOrderDialog editOrderDialog = new EditOrderDialog(getFrame());

                editOrderDialog.setOrderId(orderId);
                editOrderDialog.setFullPaymentForced(isCasual);
                editOrderDialog.setVisible(true);
            }
        }

        /*
         * Finally, closes the attendance.
         */
        try {
            attendancesService.checkOut(attendanceId);
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
