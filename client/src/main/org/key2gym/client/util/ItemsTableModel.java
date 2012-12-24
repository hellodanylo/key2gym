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
package org.key2gym.client.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.key2gym.business.api.dtos.ItemDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemsTableModel extends AbstractTableModel {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

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
            return strings.getString("Text.Title");
        } else if (columns[columnIndex].equals(Column.PRICE)) {
            return strings.getString("Text.Price");
        } else if(columns[columnIndex].equals(Column.BARCODE)) {
            return strings.getString("Text.Barcode");
        } else if(columns[columnIndex].equals(Column.QUANTITY)) {
            return strings.getString("Text.Quantity");
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
