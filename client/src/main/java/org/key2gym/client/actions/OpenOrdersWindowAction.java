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
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.client.MainFrame;
import org.key2gym.client.dialogs.AbstractDialog;
import org.key2gym.client.dialogs.PickDateDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OpenOrdersWindowAction extends BasicAction {

    public OpenOrdersWindowAction() {
        setAccelerationKey(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.ALT_MASK));
        setText(getString("Text.Orders"));
    }

    @Override
    public void onActionPerformed(ActionEvent e) throws BusinessException, ValidationException, SecurityViolationException {

        PickDateDialog pickDateDialog = new PickDateDialog(getFrame());
        pickDateDialog.setVisible(true);

        if (pickDateDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
            return;
        }

        MainFrame.getInstance().openOrdersTabForDate(pickDateDialog.getDate());
    }
}