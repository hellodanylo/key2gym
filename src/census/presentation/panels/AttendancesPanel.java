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
package census.presentation.panels;

import census.business.AttendancesService;
import census.business.SessionsService;
import census.business.StorageService;
import census.business.api.SecurityException;
import census.business.dto.AttendanceDTO;
import census.presentation.util.AttendancesTableCellRenderer;
import census.presentation.util.AttendancesTableModel;
import census.presentation.util.AttendancesTableModel.Column;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AttendancesPanel extends JPanel {

    /**
     * Creates new AttendancesPanel.
     */
    public AttendancesPanel() {
        attendancesService = AttendancesService.getInstance();
        strings = ResourceBundle.getBundle("census/presentation/resources/Strings");

        initComponents();
        buildPanel();

        observer = new AttendancesPanel.AttendancesObserver();
    }

    /**
     * Initializes the panel's components.
     */                       
    private void initComponents() {

        /*
         * Attendances journal
         */
        attendancesScrollPane = new javax.swing.JScrollPane();
        attendancesTable = new javax.swing.JTable();
        
        /*
         * Columns of the attendances journal
         */
        Column[] attendancesTableColumns =
                new Column[]{
            Column.BEGIN,
            Column.ID,
            Column.CLIENT_ID,
            Column.CLIENT_FULL_NAME,
            Column.KEY,
            Column.END
        };

        attendancesTableModel = new AttendancesTableModel(attendancesTableColumns);
        attendancesTable.setModel(attendancesTableModel);
        attendancesTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
        attendancesTable.setDefaultRenderer(String.class, new AttendancesTableCellRenderer());
        
        /*
         * Attendances counter
         */
        attendancesCountTextField = new javax.swing.JTextField();
        attendancesCountTextField.setEditable(false);
        attendancesCountTextField.setColumns(5);
    }                   
    
    /**
     * Builds the panel by placing components on it.
     */
    private void buildPanel() {
        FormLayout layout = new FormLayout("5dlu, default, 3dlu, fill:default, default:grow", "5dlu, default, 3dlu, fill:default:grow");
        setLayout(layout);
        
        add(new JLabel(strings.getString("Label.Attendances")), CC.xy(2, 2));
        add(attendancesCountTextField, CC.xy(4, 2));
        add(attendancesScrollPane, CC.xywh(1, 4, 5, 1));
    }

    /**
     * Called when the attendances table looses its focus.
     * @param evt the focus event
     */
    private void attendancesTableFocusLost(FocusEvent evt) {
        attendancesTable.clearSelection();
    }

    /**
     * Returns currently selected attendance.
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
     * @return the date
     */
    public DateMidnight getDate() {
        return date;
    }

    /**
     * Sets the current journal's date.
     * @param date the date to use
     * @throws SecurityException if the access to the date was denied
     */
    public void setDate(DateMidnight date) throws SecurityException {
        this.date = date;

        reloadPanel();
    }

    /**
     * Reloads the data.
     * @throws SecurityException if the access to the current date was denied 
     */
    private void reloadPanel() throws SecurityException {
        List<AttendanceDTO> attendances;
        attendances = attendancesService.findAttendancesByDate(date);

        attendancesTableModel.setAttendances(attendances);
        attendancesCountTextField.setText(String.valueOf(attendances.size()));
    }

    /**
     * Used to receive notifications from the business tier.
     */
    private class AttendancesObserver implements Observer {

        public AttendancesObserver() {
            registerSelf();
        }

        private void registerSelf() {
            StorageService.getInstance().addObserver(this);
        }

        @Override
        public void update(Observable o, Object arg) {
            if (SessionsService.getInstance().hasOpenSession()) {
                try {
                    reloadPanel();
                } catch (SecurityException ex) {
                    Logger.getLogger(this.getClass().getName()).error("Unexpected SecurityException", ex);
                }
            }
        }
    }
    
    /*
     * Presentation
     */
    private ResourceBundle strings;
    private AttendancesTableModel attendancesTableModel;
    private DateMidnight date;
    private JTextField attendancesCountTextField;
    private JScrollPane attendancesScrollPane;
    private JTable attendancesTable;
    
    /*
     * Business
     */
    private AttendancesObserver observer;
    private AttendancesService attendancesService;                 
}