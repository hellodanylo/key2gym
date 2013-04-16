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
package org.key2gym.client.dialogs.actions;

import java.util.ResourceBundle;

import javax.swing.AbstractAction;

import org.key2gym.client.dialogs.AbstractDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public abstract class DialogAction extends AbstractAction {

    public DialogAction(AbstractDialog dialog) {
        this.dialog = dialog;
        this.strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");
    }

    /**
     * Gets the l15d string for the key.
     *
     * @param key the key to look up the string
     * @return the l15d value
     */
    protected String getString(String key) {
        return strings.getString(key);
    }

    /**
     * Gets the dialog's strings bundle.
     *
     * @return the strings bundle
     */
    protected ResourceBundle getStrings() {
        return strings;
    }

    public AbstractDialog getDialog() {
        return dialog;
    }
    
    private AbstractDialog dialog;
    private ResourceBundle strings;
}
