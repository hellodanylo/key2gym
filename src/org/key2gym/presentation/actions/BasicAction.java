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
import org.key2gym.business.api.SessionListener;
import org.key2gym.presentation.MainFrame;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Custom generic class for actions
 *
 * @author Danylo Vashchilenko
 *
 */
public abstract class BasicAction extends AbstractAction {

    public BasicAction() {
        strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
        
        setEnabled(false);
        
        SessionsService.getInstance().addListener(new SessionListener() {

            @Override
            public void sessionOpened() {
                onSessionOpened();
            }

            @Override
            public void sessionClosed() {
                onSessionClosed();
            }

            @Override
            public void sessionChanged() {
                onSessionChanged();
            }
        });
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
    
    /**
     * Called after the session has changed.
     */
    protected void onSessionChanged() {
        
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
