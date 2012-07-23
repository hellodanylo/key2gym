/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.ItemDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemsTableModel extends AbstractTableModel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public enum Column {TITLE, PRICE, BARCODE, QUANTITY};
    
    private List<ItemDTO> items;
    private Column[] columns;
    
    public ItemsTableModel(Column[] columns) {
        this(columns, new LinkedList<ItemDTO>());
    }
    
    public ItemsTableModel(Column[] columns, List<ItemDTO> items) {
        this.columns = columns;
        this.items = items;
    }
    
    public void setItems(List<ItemDTO> items) {
        this.items = items;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.TITLE)) {
            return bundle.getString("Text.Title");
        } else if (columns[columnIndex].equals(Column.PRICE)) {
            return bundle.getString("Text.Price");
        } else if(columns[columnIndex].equals(Column.BARCODE)) {
            return bundle.getString("Text.Barcode");
        } else if(columns[columnIndex].equals(Column.QUANTITY)) {
            return bundle.getString("Text.Quantity");
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
        ItemDTO item = items.get(rowIndex);
        if (columns[columnIndex].equals(Column.TITLE)) {
            return item.getTitle();
        } else if (columns[columnIndex].equals(Column.PRICE)) {
            return item.getPrice().setScale(2).toPlainString();
        } else if (columns[columnIndex].equals(Column.BARCODE)) {
            return item.getBarcode() == null ? "" : item.getBarcode().toString();
        } else if (columns[columnIndex].equals(Column.QUANTITY)) {
            return item.getQuantity() == null ? "" : item.getQuantity().toString();
        }
        throw new IllegalArgumentException();
    }
}
