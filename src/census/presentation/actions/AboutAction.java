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

import census.presentation.dialogs.AboutDialog;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AboutAction extends CensusAction {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public AboutAction() {
        setText(bundle.getString("Text.About"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AboutDialog aboutDialog = new AboutDialog(getFrame());
        aboutDialog.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
