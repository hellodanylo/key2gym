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

import java.awt.Window;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.key2gym.business.dto.CashAdjustmentDTO;
import org.key2gym.business.dto.ItemDTO;
import org.key2gym.business.dto.KeyDTO;
import org.key2gym.business.dto.SubscriptionDTO;
import org.key2gym.presentation.dialogs.FormDialog;
import org.key2gym.presentation.dialogs.actions.CancelAction;
import org.key2gym.presentation.panels.forms.CashAdjustmentFormPanel;
import org.key2gym.presentation.panels.forms.ItemFormPanel;
import org.key2gym.presentation.panels.forms.KeyFormPanel;
import org.key2gym.presentation.panels.forms.SubscriptionFormPanel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FormPanelDialogsFactory {

    public static FormDialog createItemEditor(Window parent, final ItemDTO item) {
        final ItemFormPanel formPanel = new ItemFormPanel();
        formPanel.setForm(item);

        final FormDialog dialog = new FormDialog(parent, formPanel);

        List<Action> actions = new LinkedList<>();

        actions.add(FormDialogActionsFactory.createItemOkAction(dialog, formPanel));
        actions.add(new CancelAction(dialog));

        dialog.setActions(actions);

        if (item.getId() == null) {
            dialog.setTitle(strings.getString("Title.ItemEditor.new"));
        } else {
            dialog.setTitle(MessageFormat.format(strings.getString("Title.ItemEditor.withTitle"), item.getTitle()));
        }

        return dialog;
    }

    public static FormDialog createSubscriptionEditor(Window parent, final SubscriptionDTO subscription) {
        final SubscriptionFormPanel formPanel = new SubscriptionFormPanel();
        formPanel.setForm(subscription);

        final FormDialog dialog = new FormDialog(parent, formPanel);

        List<Action> actions = new LinkedList<>();

        actions.add(FormDialogActionsFactory.createSubscriptionOkAction(dialog, formPanel));
        actions.add(new CancelAction(dialog));

        dialog.setActions(actions);

        if (subscription.getId() == null) {
            dialog.setTitle(strings.getString("Title.SubscriptionEditor.new"));
        } else {
            dialog.setTitle(MessageFormat.format(strings.getString("Title.SubscriptionEditor.withTitle"), subscription.getTitle()));
        }

        return dialog;
    }

    public static FormDialog createCashAdjustmentEditor(Window parent, final CashAdjustmentDTO cashAdjustment) {
        final CashAdjustmentFormPanel formPanel = new CashAdjustmentFormPanel();
        formPanel.setForm(cashAdjustment);

        final FormDialog dialog = new FormDialog(parent, formPanel);

        List<Action> actions = new LinkedList<>();

        actions.add(FormDialogActionsFactory.createCashAdjustmentOkAction(dialog, formPanel));
        actions.add(new CancelAction(dialog));

        dialog.setActions(actions);

        dialog.setTitle(strings.getString("Title.CashAdjustmentEditor"));

        return dialog;
    }

    public static FormDialog createKeyEditor(Window parent, final KeyDTO key) {
        final KeyFormPanel formPanel = new KeyFormPanel();
        formPanel.setForm(key);

        final FormDialog dialog = new FormDialog(parent, formPanel);

        List<Action> actions = new LinkedList<>();

        actions.add(FormDialogActionsFactory.createKeyOkAction(dialog, formPanel));
        actions.add(new CancelAction(dialog));

        dialog.setActions(actions);

        if(key.getId() == null) {
            dialog.setTitle(strings.getString("Title.KeyEditor.new"));
        } else {
            dialog.setTitle(MessageFormat.format(strings.getString("Title.KeyEditor.withTitle"), key.getTitle()));
        }

        return dialog;
    }
    private static ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
}
