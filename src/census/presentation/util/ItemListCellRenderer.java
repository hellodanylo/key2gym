/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.ItemDTO;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author daniel
 */
public class ItemListCellRenderer implements ListCellRenderer {
    
    private ListCellRenderer renderer;
    
    public ItemListCellRenderer() {
        renderer = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return renderer.getListCellRendererComponent(list, value instanceof ItemDTO ? ((ItemDTO)value).getTitle() : value, index, isSelected, cellHasFocus);
    }
}
