/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.presentation.util;

import census.business.dto.TimeRangeDTO;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Danylo Vashchilenko
 */
public class TimeRangeListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof TimeRangeDTO) {
            TimeRangeDTO timeRange = (TimeRangeDTO)value;
            value = timeRange.getBegin().toString("HH-mm-ss") + " - " + timeRange.getEnd().toString("HH-mm-ss");
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
