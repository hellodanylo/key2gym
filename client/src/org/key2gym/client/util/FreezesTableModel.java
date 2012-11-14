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
import org.key2gym.business.api.dtos.FreezeDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FreezesTableModel extends AbstractTableModel {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");
    
    public enum Column {CLIENT_FULL_NAME, DATE_ISSUED, DAYS, DATE_EXPIRED, ADMINISTRATOR_FULL_NAME, NOTE};

    private List<FreezeDTO> freezes;
    private Column[] columns;
    
    public FreezesTableModel(final Column[] columns) {
        this(columns, new LinkedList<FreezeDTO>());
    }

    public FreezesTableModel(Column[] columns, List<FreezeDTO> freezes) {
        this.columns = columns;
        this.freezes = freezes;
    }
    
    public void setFreezes(List<FreezeDTO> freezes) {
        this.freezes = freezes;
        fireTableDataChanged();
    }

    public FreezeDTO getFreezeAt(int index) {
        return freezes.get(index);
    }

    @Override
    public int getRowCount() {
        return freezes.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FreezeDTO freeze = freezes.get(rowIndex);
        
        if (columns[columnIndex].equals(Column.CLIENT_FULL_NAME)) {
            return freeze.getClientFullName();
        } else if (columns[columnIndex].equals(Column.DATE_ISSUED)) {
            return freeze.getDateIssued().toString("dd-MM-yyyy");
        } else if (columns[columnIndex].equals(Column.DAYS)) {
            return freeze.getDays().toString();
        } else if (columns[columnIndex].equals(Column.DATE_EXPIRED)) {
            return freeze.getDateIssued().plusDays(freeze.getDays()).toString("dd-MM-yyyy");
        } else if (columns[columnIndex].equals(Column.ADMINISTRATOR_FULL_NAME)) {
            return freeze.getAdministratorFullName();
        } else if(columns[columnIndex].equals(Column.NOTE)) {
            return freeze.getNote();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.CLIENT_FULL_NAME)) {
            return strings.getString("Text.Client");
        } else if (columns[columnIndex].equals(Column.DATE_ISSUED)) {
            return strings.getString("Text.Issued");
        } else if (columns[columnIndex].equals(Column.DAYS)) {
            return strings.getString("Text.Days");
        } else if (columns[columnIndex].equals(Column.DATE_EXPIRED)) {
            return strings.getString("Text.Expiration");
        } else if (columns[columnIndex].equals(Column.ADMINISTRATOR_FULL_NAME)) {
            return strings.getString("Text.Administrator");
        } else if(columns[columnIndex].equals(Column.NOTE)) {
            return strings.getString("Text.Note");
        }
        throw new IndexOutOfBoundsException();
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
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }
}
