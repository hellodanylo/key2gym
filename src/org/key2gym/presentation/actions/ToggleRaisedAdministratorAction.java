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
package org.key2gym.presentation.actions;

import org.key2gym.business.SessionsService;
import org.key2gym.business.StorageService;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.TemporalySwapAdministratorDialog;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ToggleRaisedAdministratorAction extends BasicAction {

    public ToggleRaisedAdministratorAction() {
        setText(getString("Text.Raise"));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            StorageService.getInstance().beginTransaction();

            if (SessionsService.getInstance().hasRaisedAdministrator()) {
                SessionsService.getInstance().dropRaisedAdministrator();
            } else {
                TemporalySwapAdministratorDialog swapAdminsitratorDialog = new TemporalySwapAdministratorDialog(getFrame());
                swapAdminsitratorDialog.setVisible(true);

                if (swapAdminsitratorDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                    StorageService.getInstance().rollbackTransaction();
                    return;
                }

                if (swapAdminsitratorDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                    throw swapAdminsitratorDialog.getException();
                }
            }

            StorageService.getInstance().commitTransaction();

        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), getString("Message.ProgramEncounteredError"), getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if (StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
        }
    }

    @Override
    protected void onSessionOpened() {
        setText(getString("Text.Raise"));
        setEnabled(true);
    }

    @Override
    protected void onSessionClosed() {
        super.onSessionClosed();
        setText(getString("Text.Raise"));
    }

    @Override
    protected void onSessionChanged() {
        Boolean raised = SessionsService.getInstance().hasRaisedAdministrator();
        setText(raised ? getString("Text.Drop") : getString("Text.Raise"));
    }
}