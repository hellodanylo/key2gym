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
import org.key2gym.client.ContextManager;
import org.key2gym.client.dialogs.OpenSessionDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ToggleShadowSessionAction extends ToggleSessionAction {

    public ToggleShadowSessionAction() {
        setText(getString("Text.Raise"));
	setEnabled(true);
    }

    @Override
    public void onActionPerformed(ActionEvent e) {
	if(ContextManager.getInstance().hasShadowContext()) {
	    ContextManager.getInstance().logout();
	} else {
	    OpenSessionDialog openSessionDialog = new OpenSessionDialog(getFrame());
            openSessionDialog.setVisible(true);
	}
    }


    @Override
    protected void onSessionOpened() {
        super.onSessionOpened();
        setText(ContextManager.getInstance().hasShadowContext() ?
                getString("Text.Drop") : getString("Text.Raise"));
    }

    @Override
    protected void onSessionClosed() {
        super.onSessionClosed();
        setText(getString("Text.Raise"));
    }    
}
