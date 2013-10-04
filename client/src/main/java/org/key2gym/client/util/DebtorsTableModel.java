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

import org.key2gym.business.api.dtos.Debtor;
import org.key2gym.client.resources.ResourcesManager;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.List;

public class DebtorsTableModel extends AbstractTableModel {

    private List<Debtor> debtorsList;

    private SimpleDateFormat lastAttendanceFormat;

    public DebtorsTableModel(List<Debtor> debtorsList) {
        this.debtorsList = debtorsList;
        this.lastAttendanceFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    public void setDebtorsList(List<Debtor> debtorsList) {
        this.debtorsList = debtorsList;
        fireTableDataChanged();
    }

    public Debtor getDebtorAt(int index) {
        return this.debtorsList.get(index);
    }

    @Override
    public int getRowCount() {
        return this.debtorsList.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Debtor debtor = this.debtorsList.get(rowIndex);

        if(columnIndex == 0) { // Client ID
            return debtor.getClientId();
        } else if(columnIndex == 1) { // Full name
            return debtor.getClientFullName();
        } else if(columnIndex == 2) { // Money balance
            return debtor.getMoneyBalance();
        } else if(columnIndex == 3) { // Last attendance date
            return debtor.getLastAttendance() == null ? ResourcesManager.getString("Text.None")
                : this.lastAttendanceFormat.format(debtor.getLastAttendance());
        } else {
            throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex == 0) { // Client ID
            return ResourcesManager.getString("Text.ID");
        } else if(columnIndex == 1) { // Full name
            return ResourcesManager.getString("Text.FullName");
        } else if(columnIndex == 2) { // Money balance
            return ResourcesManager.getString("Text.MoneyBalance");
        } else if(columnIndex == 3) { // Last attendance date
            return ResourcesManager.getString("Text.LastAttendance");
        } else {
            throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        }
    }
}
