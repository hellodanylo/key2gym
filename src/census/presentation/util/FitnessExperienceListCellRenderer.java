/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
