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
public class SexListCellRenderer extends DefaultListCellRenderer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof ClientProfileDTO.Sex) {
            ClientProfileDTO.Sex sex = (ClientProfileDTO.Sex) value;
            if (sex.equals(ClientProfileDTO.Sex.MALE)) {
                value = bundle.getString("Text.Male");
            } else if (sex.equals(ClientProfileDTO.Sex.FEMALE)) {
                value = bundle.getString("Text.Female");
            } else {
                value = bundle.getString("Text.Unknown");
            }
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
