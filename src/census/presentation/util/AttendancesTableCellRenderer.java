/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AttendancesTableCellRenderer implements TableCellRenderer {

    private DefaultTableCellRenderer renderer;
    private Color openAttendanceColor;

    public AttendancesTableCellRenderer() {
        renderer = new DefaultTableCellRenderer();
        openAttendanceColor = new Color(211, 255, 130);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(!(table.getModel() instanceof AttendancesTableModel)) {
            throw new IllegalArgumentException("The table's model is not of expected type.");
        }
        
        //renderer.setOpaque(false);
        
        if (row != -1) {
            renderer.setBackground(((AttendancesTableModel)table.getModel()).getAttendanceAt(row).getDateTimeEnd() == null
                    ? openAttendanceColor : Color.white);
        }
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
