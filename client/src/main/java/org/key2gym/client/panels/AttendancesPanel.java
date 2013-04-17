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
package org.key2gym.client.panels;

import java.awt.event.FocusEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.dtos.AttendanceDTO;
import org.key2gym.business.api.services.AttendancesService;
import org.key2gym.client.ContextManager;
import org.key2gym.client.DataRefreshPulse;
import org.key2gym.client.renderers.AttendancesTableCellRenderer;
import org.key2gym.client.util.AttendancesTableModel;
import org.key2gym.client.util.AttendancesTableModel.Column;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * @author Danylo Vashchilenko
 */
public class AttendancesPanel extends JPanel {

    /**
     * Creates new AttendancesPanel.
     */
    public AttendancesPanel() {
        attendancesService = ContextManager.lookup(AttendancesService.class);
        strings = ResourceBundle
                .getBundle("org/key2gym/client/resources/Strings");

        initComponents();
        buildPanel();

        DataRefreshPulse.getInstance().addObserver(new DataRefreshObserver());
    }

    /**
     * Initializes the panel's components.
     */
    private void initComponents() {

        /*
         * Attendances journal
         */
        attendancesScrollPane = new JScrollPane();
        attendancesTable = new JTable();

        /*
         * Columns of the attendances journal
         */
        Column[] attendancesTableColumns = new Column[] { Column.BEGIN,
                Column.ID, Column.CLIENT_ID, Column.CLIENT_FULL_NAME,
                Column.KEY, Column.END };

        attendancesTableModel = new AttendancesTableModel(
                attendancesTableColumns);
        attendancesTable.setModel(attendancesTableModel);
        attendancesTable
                .setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        attendancesTable.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                attendancesTableFocusLost(evt);
            }
        });
        attendancesScrollPane.setViewportView(attendancesTable);

        /*
         * This renderer highlights open attendances
         */
        attendancesTable.setDefaultRenderer(String.class,
                new AttendancesTableCellRenderer());

        /*
         * Attendances counter
         */
        attendancesCountTextField = new javax.swing.JTextField();
        attendancesCountTextField.setEditable(false);
        attendancesCountTextField.setColumns(8);
    }

    /**
     * Builds the panel by placing components on it.
     */
    private void buildPanel() {
        FormLayout layout = new FormLayout(
                "5dlu, default, 3dlu, fill:default, default:grow",
                "5dlu, default, 3dlu, fill:default:grow");
        setLayout(layout);

        add(new JLabel(strings.getString("Label.Attendances")), CC.xy(2, 2));
        add(attendancesCountTextField, CC.xy(4, 2));
        add(attendancesScrollPane, CC.xywh(1, 4, 5, 1));
    }

    /**
     * Called when the attendances table looses its focus.
     * 
     * @param evt
     *            the focus event
     */
    private void attendancesTableFocusLost(FocusEvent evt) {
        attendancesTable.clearSelection();
    }

    /**
     * Returns currently selected attendance.
     * 
     * @return the selected attendance or null, if none is selected
     */
    public AttendanceDTO getSelectedAttendance() {
        int index = attendancesTable.getSelectedRow();
        if (index == -1) {
            return null;
        }
        return attendancesTableModel.getAttendanceAt(index);
    }

    /**
     * Gets the current journal's date.
     * 
     * @return the date
     */
    public DateMidnight getDate() {
        return date;
    }

    /**
     * Sets the current journal's date.
     * 
     * @param date
     *            the date to use
     * @throws SecurityViolationException
     *             if the access to the date was denied
     */
    public void setDate(DateMidnight date) throws SecurityViolationException {
        this.date = date;

        refresh();
    }

    private void refresh() throws SecurityViolationException {
        refreshData();
        refreshGUI();
    }

    /**
     * Reloads the data.
     * 
     * @throws SecurityViolationException
     *             if the access to the current date was denied
     */
    private void refreshData() throws SecurityViolationException {
        attendances = attendancesService.findAttendancesByDate(date);
    }

    private void refreshGUI() {
        attendancesTableModel.setAttendances(attendances);
        attendancesCountTextField.setText(String.valueOf(attendances.size()));
    }

    /**
     * Used to refresh the attendances at the data refresh rate.
     * 
     * TODO: do proper SecurityViolationException handling (e.g. closing itself)
     */
    private class DataRefreshObserver implements Observer {

        @Override
        public void update(Observable observable, Object arg) {

            if (date == null || !ContextManager.getInstance().isContextAvailable()) {
                return;
            }

            /*
             * Loads the attendances synchronously on the timer's thread.
             */
            try {
                refreshData();
            } catch (SecurityViolationException ex) {
                Logger.getLogger(AttendancesPanel.class).error(
                        "Failed to refresh the attendances!", ex);
            }

            /*
             * Updates the GUI asynchronously on the Swing thread.
             */
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshGUI();
                }
            });
        }
    }

    /*
     * Presentation
     */
    private List<AttendanceDTO> attendances;
    private ResourceBundle strings;
    private AttendancesTableModel attendancesTableModel;
    private DateMidnight date;
    private JTextField attendancesCountTextField;
    private JScrollPane attendancesScrollPane;
    private JTable attendancesTable;
    /*
     * Business
     */
    private AttendancesService attendancesService;
}
