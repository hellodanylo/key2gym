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
package census.presentation.actions;

import census.business.SessionsService;
import census.business.StorageService;
import census.presentation.dialogs.AbstractDialog;
import census.presentation.dialogs.ManageSubscriptionsDialog;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageSubscriptionsAction extends BasicAction {

    public ManageSubscriptionsAction() {
        setText(getString("Text.Subscriptions"));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            ManageSubscriptionsDialog manageSubscriptionsDialog = new ManageSubscriptionsDialog(getFrame());
            manageSubscriptionsDialog.setVisible(true);

            if (manageSubscriptionsDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw manageSubscriptionsDialog.getException();
            }

            if (manageSubscriptionsDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            storageService.commitTransaction();

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
        setEnabled(SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL));
    }

    @Override
    protected void onSessionChanged() {
        setEnabled(SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL));
    }
}
