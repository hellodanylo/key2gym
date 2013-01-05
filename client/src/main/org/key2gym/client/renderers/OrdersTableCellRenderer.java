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

package org.key2gym.client.renderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.key2gym.business.api.dtos.OrderDTO;
import org.key2gym.client.util.OrdersTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OrdersTableCellRenderer implements TableCellRenderer {
    private DefaultTableCellRenderer renderer;
    private Color unpaidFinancialActivityColor;

    public OrdersTableCellRenderer() {
        renderer = new DefaultTableCellRenderer();
        unpaidFinancialActivityColor = new Color(255, 173, 206);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(!(table.getModel() instanceof OrdersTableModel)) { 
            throw new IllegalArgumentException("The table's model is not of expected type.");
        }
        
        if (row != -1) {
            OrderDTO financialActivity = ((OrdersTableModel)table.getModel()).getOrderAt(row);
            renderer.setBackground(financialActivity.getTotal().compareTo(financialActivity.getPayment()) > 0
                    ? unpaidFinancialActivityColor : Color.white);
        }
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
