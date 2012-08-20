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

import census.business.CashService;
import census.business.SessionsService;
import census.business.StorageService;
import census.presentation.MainFrame;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.PickDateDialog;
import census.presentation.dialogs.editors.CashAdjustmentEditorDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ManageCashAction extends CensusAction implements Observer {

    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public ManageCashAction() {
        if (!Beans.isDesignTime()) {
            update(null, null);
        }

        setText(bundle.getString("Text.Cash"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StorageService storageService = null;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();

            PickDateDialog pickDateDialog = new PickDateDialog(getFrame());
            pickDateDialog.setVisible(true);

            if (pickDateDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw pickDateDialog.getException();
            }

            if (pickDateDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            CashAdjustmentEditorDialog cashAdjustmentEditorDIalog = new CashAdjustmentEditorDialog(CashService.getInstance().getAdjustmentByDate(pickDateDialog.getDate()));
            cashAdjustmentEditorDIalog.setVisible(true);

            if (cashAdjustmentEditorDIalog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw cashAdjustmentEditorDIalog.getException();
            }

            if (cashAdjustmentEditorDIalog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            storageService.commitTransaction();
        } catch (census.business.api.SecurityException ex) {
            MainFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
            if (StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if (StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
            return;
        }
    }

    @Override
    public final void update(Observable o, Object arg) {
        if (o == null) {
            SessionsService.getInstance().addObserver(this);
        }
        Boolean hasSessionAndAllPermissions = SessionsService.getInstance().hasOpenSession()
                && SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL);
        setEnabled(hasSessionAndAllPermissions);
    }
}
