/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.AttendanceDTO;
import census.business.dto.FinancialActivityDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FinancialActivitiesTableModel extends AbstractTableModel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public enum Column {ID, SUBJECT, TOTAL, PAID};
    private List<FinancialActivityDTO> financialActivities;
    private Column[] columns;

    public FinancialActivitiesTableModel(Column[] columns) {
        this(columns, new LinkedList<FinancialActivityDTO>());
    }

    public FinancialActivitiesTableModel(Column[] columns, List<FinancialActivityDTO> attendances) {
        this.columns = columns;
        this.financialActivities = attendances;
    }

    public void setFinancialActivities(List<FinancialActivityDTO> financialActivities) {
        this.financialActivities = financialActivities;
        fireTableDataChanged();
    }

    public FinancialActivityDTO getFinancialActivityAt(int index) {
        return financialActivities.get(index);
    }

    @Override
    public int getRowCount() {
        return financialActivities.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FinancialActivityDTO financialActivity = financialActivities.get(rowIndex);
        if (columns[columnIndex].equals(Column.ID)) {
            return financialActivity.getId().toString();
        } else if (columns[columnIndex].equals(Column.SUBJECT)) {
            String subject;
            if (financialActivity.getAttendanceId() != null) {
                subject = java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("census/presentation/resources/Strings").getString("Text.Attendance.withIDAndKey"), new Object[] {financialActivity.getAttendanceId(), financialActivity.getKeyTitle()});
            } else if (financialActivity.getClientId() != null) {
                subject = java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("census/presentation/resources/Strings").getString("Text.Client.withFullNameAndID"), new Object[] {financialActivity.getClientFullName(), financialActivity.getClientId()});
            } else {
                subject = bundle.getString("Text.Other");
            }
            return subject;
        } else if (columns[columnIndex].equals(Column.TOTAL)) {
            return financialActivity.getTotal().toPlainString();
        } else if (columns[columnIndex].equals(Column.PAID)) {
            return financialActivity.getPayment().toPlainString();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.ID)) {
            return bundle.getString("Text.ID");
        } else if (columns[columnIndex].equals(Column.SUBJECT)) {
            return bundle.getString("Text.Subject");
        } else if (columns[columnIndex].equals(Column.TOTAL)) {
            return bundle.getString("Text.Total");
        } else if (columns[columnIndex].equals(Column.PAID)) {
            return bundle.getString("Text.Paid");
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