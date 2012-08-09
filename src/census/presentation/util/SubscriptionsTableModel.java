/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.presentation.util;

import census.business.TimeSplitsService;
import census.business.dto.SubscriptionDTO;
import census.business.dto.TimeSplitDTO;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SubscriptionsTableModel extends AbstractTableModel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public enum Column {TITLE, PRICE, BARCODE, UNITS, TERM_DAYS, TERM_MONTHS, TERM_YEARS, TIME_RANGE};
    
    private Column[] columns;
    private List<SubscriptionDTO> subscriptions;
    private List<TimeSplitDTO> timeRanges;
    
    public SubscriptionsTableModel(Column[] columns) {
        this.columns = columns;
        timeRanges = TimeSplitsService.getInstance().getAll();
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
            return bundle.getString("Text.Title");
        } else if (columns[columnIndex].equals(Column.PRICE)) {
            return bundle.getString("Text.Price");
        } else if(columns[columnIndex].equals(Column.BARCODE)) {
            return bundle.getString("Text.Barcode");
        } else if(columns[columnIndex].equals(Column.TERM_DAYS)) {
            return bundle.getString("Text.D");
        } else if(columns[columnIndex].equals(Column.TERM_MONTHS)) {
            return bundle.getString("Text.M");
        } else if(columns[columnIndex].equals(Column.TERM_YEARS)) {
            return bundle.getString("Text.Y");
        } else if(columns[columnIndex].equals(Column.UNITS)) {
            return bundle.getString("Text.Units");
        }else if(columns[columnIndex].equals(Column.TIME_RANGE)) {
            return bundle.getString("Text.TimeRange");
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
