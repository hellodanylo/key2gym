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

import census.presentation.CensusFrame;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Custom generic class for actions
 *
 * @author Danylo Vashchilenko
 *
 */
public abstract class CensusAction extends AbstractAction implements Observer {

    public CensusAction() {
        
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

    protected CensusFrame getFrame() {
        return CensusFrame.getInstance();
    }

    public static final String ACTION_GLOBAL = "ACTION_GLOBAL";
    public static final String ACTION_CONTEXT = "ACTION_CONTEXT";
}
