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
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.CashAdjustmentDTO;
import org.key2gym.business.api.remote.CashServiceRemote;
import org.key2gym.client.ContextManager;
import org.key2gym.client.dialogs.AbstractDialog;
import org.key2gym.client.dialogs.FormDialog;
import org.key2gym.client.dialogs.PickDateDialog;
import org.key2gym.client.factories.FormPanelDialogsFactory;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageCashAction extends BasicAction {

    public ManageCashAction() {
        setText(getString("Text.Cash"));
    }

    @Override
    public void onActionPerformed(ActionEvent e) throws BusinessException, ValidationException, SecurityViolationException {
        PickDateDialog pickDateDialog = new PickDateDialog(getFrame());
        pickDateDialog.setVisible(true);

        if (pickDateDialog.getResult().equals(AbstractDialog.Result.EXCEPTION)) {
            throw pickDateDialog.getException();
        }

        if (pickDateDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
            return;
        }

        CashAdjustmentDTO cashAdjustment = ContextManager.lookup(CashServiceRemote.class).getAdjustmentByDate(pickDateDialog.getDate());
        FormDialog dialog = FormPanelDialogsFactory.createCashAdjustmentEditor(getFrame(), cashAdjustment);
        dialog.setVisible(true);

        if (dialog.getResult().equals(AbstractDialog.Result.EXCEPTION)) {
            throw dialog.getException();
        }

        if (dialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
            return;
        }
    }
}
