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

import org.key2gym.business.CashService;
import org.key2gym.business.SessionsService;
import org.key2gym.business.StorageService;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.PickDateDialog;
import org.key2gym.presentation.dialogs.editors.CashAdjustmentEditorDialog;
import org.key2gym.presentation.util.UserExceptionHandler;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageCashAction extends BasicAction {

    public ManageCashAction() {
        setText(getString("Text.Cash"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            PickDateDialog pickDateDialog = new PickDateDialog(getFrame());
            pickDateDialog.setVisible(true);

            if (pickDateDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw pickDateDialog.getException();
            }

            if (pickDateDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            CashAdjustmentEditorDialog cashAdjustmentEditorDIalog = new CashAdjustmentEditorDialog(CashService.getInstance().getAdjustmentByDate(pickDateDialog.getDate()));
            cashAdjustmentEditorDIalog.setVisible(true);

            if (cashAdjustmentEditorDIalog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw cashAdjustmentEditorDIalog.getException();
            }

            if (cashAdjustmentEditorDIalog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            storageService.commitTransaction();
        } catch (org.key2gym.business.api.SecurityException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            if (StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
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
