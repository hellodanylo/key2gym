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
package org.key2gym.client.actions;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.remote.OrdersServiceRemote;
import org.key2gym.client.ContextManager;
import org.key2gym.client.dialogs.AbstractDialog;
import org.key2gym.client.dialogs.CheckInDialog;
import org.key2gym.client.dialogs.EditOrderDialog;
import org.key2gym.client.dialogs.RegisterClientDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class RegisterClientAction extends BasicAction {

    public RegisterClientAction() {
        setText(getString("Text.Register"));
        setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/registerClient.png")));
    }

    /**
     * Processes an event.
     *
     * @param event the event
     */
    @Override
    public void onActionPerformed(ActionEvent e) throws BusinessException, ValidationException, SecurityViolationException {
        RegisterClientDialog registerClientDialog;

        registerClientDialog = new RegisterClientDialog(getFrame());
        registerClientDialog.setVisible(true);

        if (registerClientDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
            return;
        }

        /*
         * If requested, creates and launches EditOrderDialog
         */
        if (registerClientDialog.isEditOrderDialogRequested()) {

            EditOrderDialog editOrderDialog = new EditOrderDialog(getFrame());

            try {
                Integer orderId = ContextManager.lookup(OrdersServiceRemote.class).findByClientIdAndDate(registerClientDialog.getClientId(), new DateMidnight(), true);
                editOrderDialog.setOrderId(orderId);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }

            editOrderDialog.setVisible(true);

            if (editOrderDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
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

            if (openAttendanceDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
                return;
            }
        }

    }
}
