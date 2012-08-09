/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.ItemDTO;
import census.business.dto.OrderLineDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OrderLinesTableModel extends AbstractTableModel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public enum Column {ITEM_TITLE, ITEM_PRICE, ITEM_ID, QUANTITY, DISCOUNT_PERCENT, DISCOUNT_TITLE, TOTAL};
    
    private List<OrderLineDTO> orderLines;
    private Column[] columns;
    
    public OrderLinesTableModel(Column[] columns) {
        this(columns, new LinkedList<OrderLineDTO>());
    }
    
    public OrderLinesTableModel(Column[] columns, List<OrderLineDTO> orderLines) {
        this.columns = columns;
        this.orderLines = orderLines;
    }
    
    public void setOrderLines(List<OrderLineDTO> orderLines) {
        this.orderLines = orderLines;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return orderLines.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.ITEM_TITLE)) {
            return bundle.getString("Text.Item");
        } else if (columns[columnIndex].equals(Column.ITEM_PRICE)) {
            return bundle.getString("Text.Price");
        } else if(columns[columnIndex].equals(Column.ITEM_ID)) {
            return bundle.getString("Text.ItemID");
        } else if(columns[columnIndex].equals(Column.QUANTITY)) {
            return bundle.getString("Text.Quantity");
        } else if(columns[columnIndex].equals(Column.DISCOUNT_PERCENT)) {
            return bundle.getString("Text.DiscountPercent");
        } else if(columns[columnIndex].equals(Column.DISCOUNT_TITLE)) {
            return bundle.getString("Text.Discount");
        } else if(columns[columnIndex].equals(Column.TOTAL)) {
            return bundle.getString("Text.Total");
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderLineDTO orderLine = orderLines.get(rowIndex);
        if (columns[columnIndex].equals(Column.ITEM_TITLE)) {
            return orderLine.getItemTitle();
        } else if (columns[columnIndex].equals(Column.ITEM_PRICE)) {
            return orderLine.getItemPrice().toPlainString();
        } else if(columns[columnIndex].equals(Column.ITEM_ID)) {
            return orderLine.getItemId().toString();
        } else if(columns[columnIndex].equals(Column.QUANTITY)) {
            return orderLine.getQuantity().toString();
        } else if(columns[columnIndex].equals(Column.DISCOUNT_PERCENT)) {
            return orderLine.getDiscountPercent().toString();
        } else if(columns[columnIndex].equals(Column.DISCOUNT_TITLE)) {
            return orderLine.getDiscountTitle();
        } else if(columns[columnIndex].equals(Column.TOTAL)) {
            return orderLine.getTotal().toPlainString();
        }
        throw new IllegalArgumentException();
    }
}
