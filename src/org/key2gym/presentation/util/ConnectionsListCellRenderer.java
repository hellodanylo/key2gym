/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.util;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.key2gym.presentation.connections.core.BasicConnection;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ConnectionsListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (BasicConnection.class.isAssignableFrom(value.getClass())) {
            value = ((BasicConnection)value).getTitle();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
