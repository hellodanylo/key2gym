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
package census.presentation.util;

import census.business.dto.AttendanceDTO;
import census.business.dto.OrderDTO;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OrdersTableModel extends AbstractTableModel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public enum Column {ID, SUBJECT, TOTAL, PAID};
    private List<OrderDTO> financialActivities;
    private Column[] columns;

    public OrdersTableModel(Column[] columns) {
        this(columns, new LinkedList<OrderDTO>());
    }

    public OrdersTableModel(Column[] columns, List<OrderDTO> attendances) {
        this.columns = columns;
        this.financialActivities = attendances;
    }

    public void setFinancialActivities(List<OrderDTO> financialActivities) {
        this.financialActivities = financialActivities;
        fireTableDataChanged();
    }

    public OrderDTO getFinancialActivityAt(int index) {
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
        OrderDTO financialActivity = financialActivities.get(rowIndex);
        if (columns[columnIndex].equals(Column.ID)) {
            return MessageFormat.format("{0}", financialActivity.getId());
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
            return bundle.getString("Text.OrderID");
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