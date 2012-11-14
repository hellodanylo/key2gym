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

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.key2gym.business.api.dtos.OrderDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OrdersTableModel extends AbstractTableModel {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

    public enum Column {ID, SUBJECT, TOTAL, PAID};
    private List<OrderDTO> orders;
    private Column[] columns;

    public OrdersTableModel(Column[] columns) {
        this(columns, new LinkedList<OrderDTO>());
    }

    public OrdersTableModel(Column[] columns, List<OrderDTO> attendances) {
        this.columns = columns;
        this.orders = attendances;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
        fireTableDataChanged();
    }

    public OrderDTO getOrderAt(int index) {
        return orders.get(index);
    }

    @Override
    public int getRowCount() {
        return orders.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderDTO order = orders.get(rowIndex);
        if (columns[columnIndex].equals(Column.ID)) {
            return MessageFormat.format("{0}", order.getId());
        } else if (columns[columnIndex].equals(Column.SUBJECT)) {
            String subject;
            if (order.getAttendanceId() != null) {
                subject = MessageFormat.format(strings.getString("Text.Attendance.withIDAndKey"), new Object[] {order.getAttendanceId(), order.getKeyTitle()});
            } else if (order.getClientId() != null) {
                subject = MessageFormat.format(strings.getString("Text.Client.withFullNameAndID"), new Object[] {order.getClientFullName(), order.getClientId()});
            } else {
                subject = strings.getString("Text.Other");
            }
            return subject;
        } else if (columns[columnIndex].equals(Column.TOTAL)) {
            return order.getTotal().toPlainString();
        } else if (columns[columnIndex].equals(Column.PAID)) {
            return order.getPayment().toPlainString();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.ID)) {
            return strings.getString("Text.OrderID");
        } else if (columns[columnIndex].equals(Column.SUBJECT)) {
            return strings.getString("Text.Subject");
        } else if (columns[columnIndex].equals(Column.TOTAL)) {
            return strings.getString("Text.Total");
        } else if (columns[columnIndex].equals(Column.PAID)) {
            return strings.getString("Text.Paid");
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