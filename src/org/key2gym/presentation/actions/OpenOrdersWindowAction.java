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

import org.key2gym.business.api.SecurityException;
import org.key2gym.presentation.MainFrame;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.PickDateDialog;
import org.key2gym.presentation.util.UserExceptionHandler;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

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
    public void actionPerformed(ActionEvent e) {

        try {

            PickDateDialog pickDateDialog = new PickDateDialog(getFrame());
            pickDateDialog.setVisible(true);

            if (pickDateDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw pickDateDialog.getException();
            }

            if (pickDateDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                return;
            }

            MainFrame.getInstance().openOrdersTabForDate(pickDateDialog.getDate());


        } catch (SecurityException ex) {
            UserExceptionHandler.getInstance().processException(ex);
        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), getString("Message.ProgramEncounteredError"), getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}