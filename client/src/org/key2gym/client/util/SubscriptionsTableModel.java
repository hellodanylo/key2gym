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


import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.key2gym.business.api.dtos.SubscriptionDTO;
import org.key2gym.business.api.dtos.TimeSplitDTO;
import org.key2gym.business.api.remote.TimeSplitsServiceRemote;
import org.key2gym.client.ContextManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SubscriptionsTableModel extends AbstractTableModel {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

    public enum Column {TITLE, PRICE, BARCODE, UNITS, TERM_DAYS, TERM_MONTHS, TERM_YEARS, TIME_RANGE};
    
    private Column[] columns;
    private List<SubscriptionDTO> subscriptions;
    private List<TimeSplitDTO> timeRanges;
    
    public SubscriptionsTableModel(Column[] columns) {
        this.columns = columns;
        timeRanges = ContextManager.lookup(TimeSplitsServiceRemote.class).getAll();
    }
    
    public void setSubscriptions(List<SubscriptionDTO> subscriptions) {
        this.subscriptions = subscriptions;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return subscriptions.size();
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
        } else if(columns[columnIndex].equals(Column.TERM_DAYS)) {
            return strings.getString("Text.D");
        } else if(columns[columnIndex].equals(Column.TERM_MONTHS)) {
            return strings.getString("Text.M");
        } else if(columns[columnIndex].equals(Column.TERM_YEARS)) {
            return strings.getString("Text.Y");
        } else if(columns[columnIndex].equals(Column.UNITS)) {
            return strings.getString("Text.Units");
        }else if(columns[columnIndex].equals(Column.TIME_RANGE)) {
            return strings.getString("Text.TimeRange");
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
        SubscriptionDTO sub = subscriptions.get(rowIndex);
        if (columns[columnIndex].equals(Column.TITLE)) {
            return sub.getTitle();
        } else if (columns[columnIndex].equals(Column.PRICE)) {
            return sub.getPrice().toPlainString();
        } else if (columns[columnIndex].equals(Column.BARCODE)) {
            return sub.getBarcode() == null ? "" : sub.getBarcode().toString();
        } else if (columns[columnIndex].equals(Column.UNITS)) {
            return sub.getUnits().toString();
        }  else if (columns[columnIndex].equals(Column.TERM_DAYS)) {
            return sub.getTermDays().toString();
        }  else if (columns[columnIndex].equals(Column.TERM_MONTHS)) {
            return sub.getTermMonths().toString();
        }  else if (columns[columnIndex].equals(Column.TERM_YEARS)) {
            return sub.getTermYears().toString();
        }  else if (columns[columnIndex].equals(Column.TIME_RANGE)) {
            for(TimeSplitDTO timeRange : timeRanges) {
                if(timeRange.getId().equals(sub.getTimeSplitId())) {
                    return timeRange.getTitle();
                }
            }
            return "";
        } 
        throw new IllegalArgumentException();
    }
}
