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
package census.presentation.util;

import census.business.dto.ClientProfileDTO;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FitnessExperienceListCellRenderer extends DefaultListCellRenderer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof ClientProfileDTO.FitnessExperience) {
            ClientProfileDTO.FitnessExperience fitnessExperience = (ClientProfileDTO.FitnessExperience) value;
            if (fitnessExperience.equals(ClientProfileDTO.FitnessExperience.NO)) {
                value = bundle.getString("Text.No");
            } else if (fitnessExperience.equals(ClientProfileDTO.FitnessExperience.UNKNOWN)) {
                value = bundle.getString("Text.Unknown");
            } else {
                value = bundle.getString("Text.Yes");
            }
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
