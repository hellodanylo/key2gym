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

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.key2gym.business.api.dtos.AttendanceDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AttendancesTableModel extends AbstractTableModel {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");
    
    public enum Column {ID, BEGIN_DATE, BEGIN, KEY, CLIENT_ID, CLIENT_FULL_NAME, END};

    private List<AttendanceDTO> attendances;
    private Column[] columns;
    
    public AttendancesTableModel(final Column[] columns) {
        this(columns, new LinkedList<AttendanceDTO>());
    }

    public AttendancesTableModel(final Column[] columns, final List<AttendanceDTO> attendances) {
        this.columns = columns;
        this.attendances = attendances;
    }
    
    public void setAttendances(final List<AttendanceDTO> attendances) {
        this.attendances = attendances;
        fireTableDataChanged();
    }

    public AttendanceDTO getAttendanceAt(int index) {
        return attendances.get(index);
    }

    @Override
    public int getRowCount() {
       return attendances.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AttendanceDTO attendance = attendances.get(rowIndex);
        if (columns[columnIndex].equals(Column.BEGIN)) {
            return MessageFormat.format("{0, time, short}", attendance.getDateTimeBegin().toDate());
        } else if (columns[columnIndex].equals(Column.CLIENT_ID)) {
            return attendance.getClientId() == null ? "" : attendance.getClientId().toString();
        } else if (columns[columnIndex].equals(Column.CLIENT_FULL_NAME)) {
            return attendance.getClientFullName() == null ? "" : attendance.getClientFullName();
        } else if (columns[columnIndex].equals(Column.KEY)) {
            return attendance.getKeyTitle();
        } else if (columns[columnIndex].equals(Column.END)) {
            return attendance.getDateTimeEnd() == null ? "" : MessageFormat.format("{0, time, short}", attendance.getDateTimeEnd().toDate());
        } else if(columns[columnIndex].equals(Column.BEGIN_DATE)) {
            return MessageFormat.format("{0, date, short}", attendance.getDateTimeBegin().toDate());
        } else if(columns[columnIndex].equals(Column.ID)) {
            return MessageFormat.format("{0}", attendance.getId());
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.BEGIN)) {
            return strings.getString("Text.Beginning");
        } else if (columns[columnIndex].equals(Column.CLIENT_ID)) {
            return strings.getString("Text.ClientID");
        }  else if (columns[columnIndex].equals(Column.CLIENT_FULL_NAME)) {
            return strings.getString("Text.ClientFullName");
        }  else if (columns[columnIndex].equals(Column.KEY)) {
            return strings.getString("Text.Key");
        }  else if (columns[columnIndex].equals(Column.END)) {
            return strings.getString("Text.Ending");
        } else if (columns[columnIndex].equals(Column.BEGIN_DATE)) {
            return strings.getString("Text.Date");
        } else if(columns[columnIndex].equals(Column.ID)) {
            return strings.getString("Text.AttendanceID");
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
