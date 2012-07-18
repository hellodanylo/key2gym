/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.presentation.util;

import census.business.dto.FinancialActivityDTO;
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FinancialActivitiesTableCellRenderer implements TableCellRenderer {
    private DefaultTableCellRenderer renderer;
    private Color unpaidFinancialActivityColor;

    public FinancialActivitiesTableCellRenderer() {
        renderer = new DefaultTableCellRenderer();
        unpaidFinancialActivityColor = new Color(255, 173, 206);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(!(table.getModel() instanceof FinancialActivitiesTableModel)) { 
            throw new IllegalArgumentException("The table's model is not of expected type.");
        }
        
        if (row != -1) {
            FinancialActivityDTO financialActivity = ((FinancialActivitiesTableModel)table.getModel()).getFinancialActivityAt(row);
            renderer.setBackground(financialActivity.getTotal().compareTo(financialActivity.getPayment()) > 0
                    ? unpaidFinancialActivityColor : Color.white);
        }
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
