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
package org.key2gym.client.util;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.ResourceBundle;

import javax.swing.FocusManager;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.key2gym.client.resources.ResourcesManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ExceptionCatchingEventQueue extends EventQueue {

    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        } catch (Throwable t) {            
            logger.error("Uncaught exception!", t);
            JOptionPane.showMessageDialog(getComponent(), strings.getString("Message.ProgramEncounteredError"), strings.getString("Title.Message"), JOptionPane.ERROR_MESSAGE);
        }
    }

    protected Component getComponent() {
        return FocusManager.getCurrentManager().getFocusedWindow();
    }
    
    ResourceBundle strings = ResourcesManager.getStrings();
    Logger logger = Logger.getLogger(ExceptionCatchingEventQueue.class);
}
