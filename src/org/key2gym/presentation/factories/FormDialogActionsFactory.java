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
package org.key2gym.presentation.factories;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.key2gym.business.CashService;
import org.key2gym.business.ItemsService;
import org.key2gym.business.SubscriptionsService;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.CashAdjustmentDTO;
import org.key2gym.business.dto.ItemDTO;
import org.key2gym.business.dto.SubscriptionDTO;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.FormDialog;
import org.key2gym.presentation.dialogs.actions.OkAction;
import org.key2gym.presentation.panels.forms.CashAdjustmentFormPanel;
import org.key2gym.presentation.panels.forms.ItemFormPanel;
import org.key2gym.presentation.panels.forms.SubscriptionFormPanel;
import org.key2gym.presentation.util.UserExceptionHandler;

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
                if(!formPanel.trySave()) {
                    return;
                }
                
                try {
                    if (item.getId() == null) {
                        ItemsService.getInstance().addItem(item);
                    } else {
                        ItemsService.getInstance().updateItem(item);
                    }
                } catch (ValidationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                } catch (org.key2gym.business.api.SecurityException ex) {
                    dialog.setResult(AbstractDialog.Result.EXCEPTION);
                    dialog.setException(new RuntimeException(ex));
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
                if(!formPanel.trySave()) {
                    return;
                }
                
                try {
                    if (subscription.getId() == null) {
                        SubscriptionsService.getInstance().addSubscription(subscription);
                    } else {
                        SubscriptionsService.getInstance().updateSubscription(subscription);
                    }
                } catch (ValidationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                } catch (org.key2gym.business.api.SecurityException ex) {
                    dialog.setResult(AbstractDialog.Result.EXCEPTION);
                    dialog.setException(new RuntimeException(ex));
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
                if(!formPanel.trySave()) {
                    return;
                }
                
                try {
                    CashService.getInstance().recordCashAdjustment(cashAdjustment);
                } catch (ValidationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                } catch (org.key2gym.business.api.SecurityException ex) {
                    dialog.setResult(AbstractDialog.Result.EXCEPTION);
                    dialog.setException(new RuntimeException(ex));
                    return;
                }

                super.actionPerformed(e);
            }
        };
    }
}