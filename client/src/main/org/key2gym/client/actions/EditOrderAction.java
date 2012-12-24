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
import org.key2gym.client.MainFrame;
import org.key2gym.client.dialogs.AbstractDialog;
import org.key2gym.client.dialogs.EditOrderDialog;
import org.key2gym.client.dialogs.PickClientDialog;
import org.key2gym.client.dialogs.PickOrderDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class EditOrderAction extends BasicAction {

    public EditOrderAction() {

        setText(getString("Text.Orders"));
        setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/order.png")));
    }

    @Override
    public void onActionPerformed(ActionEvent e) throws BusinessException, ValidationException, SecurityViolationException {
        OrdersServiceRemote ordersService = ContextManager.lookup(OrdersServiceRemote.class);

        Integer orderId;

        if (e.getActionCommand().equals(ACTION_CONTEXT)) {
            orderId = MainFrame.getInstance().getSelectedOrder().getId();
        } else {
            PickOrderDialog pickOrderDialog = new PickOrderDialog(getFrame());
            pickOrderDialog.setVisible(true);

            if (pickOrderDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
                return;
            }

            if (pickOrderDialog.isClient()) {
                PickClientDialog pickClientDialog = new PickClientDialog(getFrame());
                pickClientDialog.setVisible(true);

                if (pickClientDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
                    return;
                }

                try {
                    orderId = ordersService.findByClientIdAndDate(pickClientDialog.getClientId(), new DateMidnight(), true);
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
    }
}
