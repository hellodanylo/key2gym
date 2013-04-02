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
package org.key2gym.client;

import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.FocusManager;
import javax.swing.JOptionPane;
import static org.key2gym.client.resources.ResourcesManager.*;
import org.key2gym.business.api.UserException;

/**
 *
 * @author Danylo Vashchilenko
 */
public class UserExceptionHandler {
    
    protected UserExceptionHandler() {
    }

    protected Component getComponent() {
        return FocusManager.getCurrentManager().getFocusedWindow();
    }
    
    public void processException(UserException ex) {
        JOptionPane.showMessageDialog(getComponent(), 
				      ex.getMessage(), 
				      getString("Title.Message"), 
				      JOptionPane.WARNING_MESSAGE);
    }
        
    /**
     * Singleton instance.
     */
    private static UserExceptionHandler instance;
    
    /**
     * Returns an instance of this class.
     * 
     * @return an instance of this class 
     */
    public static UserExceptionHandler getInstance() {
        if(instance == null) {
            instance = new UserExceptionHandler();
        }
        
        return instance;
    }
}