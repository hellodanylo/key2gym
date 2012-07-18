/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.KeyDTO;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author daniel
 */
public class KeyListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, value instanceof KeyDTO ? ((KeyDTO)value).getTitle() : value, index, isSelected, cellHasFocus);
    }
}
