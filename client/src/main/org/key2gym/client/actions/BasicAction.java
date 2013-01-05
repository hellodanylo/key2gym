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
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.client.ContextManager;
import org.key2gym.client.MainFrame;
import org.key2gym.client.UserExceptionHandler;

/**
 * Custom generic class for actions
 *
 * @author Danylo Vashchilenko
 *
 */
public abstract class BasicAction extends AbstractAction {

    public BasicAction() {
        strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

        setEnabled(false);

        ContextManager.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable contextManager, Object obj) {
                if (ContextManager.getInstance().isContextAvailable()) {
                    onSessionOpened();
                } else {
                    onSessionClosed();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        try {
            onActionPerformed(action);
        } catch (BusinessException | SecurityViolationException | ValidationException ex) {
            UserExceptionHandler.getInstance().processException(ex);
        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), getString("Message.ProgramEncounteredError"), getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public void setText(String text) {
        putValue(AbstractAction.NAME, text);
    }

    public void setIcon(Icon icon) {
        putValue(AbstractAction.LARGE_ICON_KEY, icon);
    }

    public void setAccelerationKey(KeyStroke key) {
        putValue(AbstractAction.ACCELERATOR_KEY, key);
    }

    public void setSelected(Boolean selected) {
        putValue(AbstractAction.SELECTED_KEY, selected);
    }

    /**
     * Called when an action is being performed.
     */
    protected abstract void onActionPerformed(ActionEvent e) throws SecurityViolationException, BusinessException, ValidationException;

    /**
     * Called after a session has been opened.
     */
    protected void onSessionOpened() {
        setEnabled(true);
    }

    /**
     * Called after the session has been closed.
     */
    protected void onSessionClosed() {
        setEnabled(false);
    }

    protected ResourceBundle getStrings() {
        return strings;
    }

    protected String getString(String key) {
        return getStrings().getString(key);
    }

    protected MainFrame getFrame() {
        return MainFrame.getInstance();
    }
    private ResourceBundle strings;
    public static final String ACTION_GLOBAL = "ACTION_GLOBAL";
    public static final String ACTION_CONTEXT = "ACTION_CONTEXT";
}
