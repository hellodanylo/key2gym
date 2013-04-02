/*
 * Copyright 2012-2013 Danylo Vashchilenko
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
import org.key2gym.business.api.dtos.KeyDTO;


/**
 *
 * @author Danylo Vashchilenko
 */
public class KeysTableModel extends AbstractTableModel {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

    public enum Column {ID, TITLE};
    
    private List<KeyDTO> keys;
    private Column[] columns;
    
    public KeysTableModel(Column[] columns) {
        this(columns, new LinkedList<KeyDTO>());
    }
    
    public KeysTableModel(Column[] columns, List<KeyDTO> items) {
        this.columns = columns;
        this.keys = items;
    }
    
    public void setKeys(List<KeyDTO> keys) {
        this.keys = keys;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return keys.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.ID)) {
            return strings.getString("Text.ID");
        } else if (columns[columnIndex].equals(Column.TITLE)) {
            return strings.getString("Text.Title");
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
        KeyDTO key = keys.get(rowIndex);
        if (columns[columnIndex].equals(Column.ID)) {
            return key.getId();
        } else if (columns[columnIndex].equals(Column.TITLE)) {
            return key.getTitle();
        }
        throw new IllegalArgumentException();
    }
}
