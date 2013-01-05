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
package org.key2gym.client.factories;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.CashAdjustmentDTO;
import org.key2gym.business.api.dtos.ItemDTO;
import org.key2gym.business.api.dtos.KeyDTO;
import org.key2gym.business.api.dtos.SubscriptionDTO;
import org.key2gym.business.api.remote.CashServiceRemote;
import org.key2gym.business.api.remote.ItemsServiceRemote;
import org.key2gym.business.api.remote.KeysServiceRemote;
import org.key2gym.business.api.remote.SubscriptionsServiceRemote;
import org.key2gym.client.ContextManager;
import org.key2gym.client.UserExceptionHandler;
import org.key2gym.client.dialogs.FormDialog;
import org.key2gym.client.dialogs.actions.OkAction;
import org.key2gym.client.panels.forms.CashAdjustmentFormPanel;
import org.key2gym.client.panels.forms.ItemFormPanel;
import org.key2gym.client.panels.forms.KeyFormPanel;
import org.key2gym.client.panels.forms.SubscriptionFormPanel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FormDialogActionsFactory {

    public static Action createItemOkAction(final FormDialog dialog, final ItemFormPanel formPanel) {
        final ItemDTO item = formPanel.getForm();

        return new OkAction(dialog) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!formPanel.trySave()) {
                    return;
                }

                try {
                    if (item.getId() == null) {
                        ContextManager.lookup(ItemsServiceRemote.class).addItem(item);
                    } else {
                        ContextManager.lookup(ItemsServiceRemote.class).updateItem(item);
                    }
                } catch (ValidationException|SecurityViolationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                }

                super.actionPerformed(e);
            }
        };
    }

    public static Action createSubscriptionOkAction(final FormDialog dialog, final SubscriptionFormPanel formPanel) {
        final SubscriptionDTO subscription = formPanel.getForm();

        return new OkAction(dialog) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!formPanel.trySave()) {
                    return;
                }

                try {
                    if (subscription.getId() == null) {
                        ContextManager.lookup(SubscriptionsServiceRemote.class).addSubscription(subscription);
                    } else {
                        ContextManager.lookup(SubscriptionsServiceRemote.class).updateSubscription(subscription);
                    }
                } catch (ValidationException | SecurityViolationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                }

                super.actionPerformed(e);
            }
        };
    }

    public static Action createKeyOkAction(final FormDialog dialog, final KeyFormPanel formPanel) {
        final KeyDTO key = formPanel.getForm();

        return new OkAction(dialog) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!formPanel.trySave()) {
                    return;
                }

                try {
                    if (key.getId() == null) {
                        ContextManager.lookup(KeysServiceRemote.class).addKey(key);
                    } else {
                        ContextManager.lookup(KeysServiceRemote.class).updateKey(key);
                    }
                } catch (ValidationException | SecurityViolationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                }
                
                super.actionPerformed(e);
            }
        };
    }

    public static Action createCashAdjustmentOkAction(final FormDialog dialog, final CashAdjustmentFormPanel formPanel) {
        final CashAdjustmentDTO cashAdjustment = formPanel.getForm();

        return new OkAction(dialog) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!formPanel.trySave()) {
                    return;
                }

                try {
                    ContextManager.lookup(CashServiceRemote.class).recordCashAdjustment(cashAdjustment);
                } catch (ValidationException | SecurityViolationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                }

                super.actionPerformed(e);
            }
        };
    }
}