/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.AttendanceDTO;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AttendancesTableModel extends AbstractTableModel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");
    
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
            return bundle.getString("Text.Beginning");
        } else if (columns[columnIndex].equals(Column.CLIENT_ID)) {
            return bundle.getString("Text.ClientID");
        }  else if (columns[columnIndex].equals(Column.CLIENT_FULL_NAME)) {
            return bundle.getString("Text.ClientFullName");
        }  else if (columns[columnIndex].equals(Column.KEY)) {
            return bundle.getString("Text.Key");
        }  else if (columns[columnIndex].equals(Column.END)) {
            return bundle.getString("Text.Ending");
        } else if (columns[columnIndex].equals(Column.BEGIN_DATE)) {
            return bundle.getString("Text.Date");
        } else if(columns[columnIndex].equals(Column.ID)) {
            return bundle.getString("Text.AttendanceID");
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
