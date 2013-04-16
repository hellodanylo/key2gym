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

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.key2gym.client.dialogs.AbstractDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OkAction extends DialogAction {
    /**
     * Creates an OkAction.
     */
    public OkAction(AbstractDialog dialog) {     
        super(dialog);
        putValue(NAME, getString("Button.Ok"));
        putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/key2gym/client/resources/ok16.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractDialog dialog = getDialog();
        
        dialog.setResult(AbstractDialog.Result.OK);
        dialog.dispose();
    }
}
