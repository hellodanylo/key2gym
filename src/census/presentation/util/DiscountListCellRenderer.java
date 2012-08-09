/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.DiscountDTO;
import census.business.dto.ItemDTO;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author daniel
 */
public class DiscountListCellRenderer implements ListCellRenderer {
    
    private ListCellRenderer renderer;
    
    public DiscountListCellRenderer() {
        renderer = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof DiscountDTO) {
            value = ((DiscountDTO)value).getTitle();
        } else if(value == null) {
            value = ResourceBundle.getBundle("census/presentation/resources/Strings").getString("Text.Discount.None");
        }
        return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
