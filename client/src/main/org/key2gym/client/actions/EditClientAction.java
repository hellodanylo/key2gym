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
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.client.dialogs.AbstractDialog;
import org.key2gym.client.dialogs.EditClientDialog;
import org.key2gym.client.dialogs.PickClientDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class EditClientAction extends BasicAction {

    public EditClientAction() {

        setText(getString("Text.Find"));
        setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/search64.png")));
    }

    @Override
    public void onActionPerformed(ActionEvent e) throws BusinessException, ValidationException, SecurityViolationException {

        final PickClientDialog pickClientDialog = new PickClientDialog(getFrame());
        pickClientDialog.setVisible(true);

        if (pickClientDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
            return;
        }

        final EditClientDialog editClientDialog = new EditClientDialog(getFrame());
        new Thread() {
            @Override
            public void run() {
                editClientDialog.setClientId(pickClientDialog.getClientId());
            }
        }.start();
        editClientDialog.setVisible(true);

        if (editClientDialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
            return;
        }
    }
}
