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

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.client.resources.ResourcesManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ReportsTableModel extends AbstractTableModel {

    public ReportsTableModel(Column[] columns) {
        this(columns, new LinkedList<ReportDTO>());
    }

    public ReportsTableModel(Column[] columns, List<ReportDTO> reports) {
        this.columns = columns;
        this.reports = reports;
    }

    public void setReports(List<ReportDTO> reports) {

	assert reports != null;

        this.reports = reports;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return reports.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.TITLE)) {
            return strings.getString("Text.Title");
        } else if (columns[columnIndex].equals(Column.ID)) {
            return strings.getString("Text.ID");
        } else if (columns[columnIndex].equals(Column.NOTE)) {
            return strings.getString("Text.Note");
        } else if (columns[columnIndex].equals(Column.DATETIME_GENERATED)) {
            return strings.getString("Text.Generated");
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
        ReportDTO report = reports.get(rowIndex);
        if (columns[columnIndex].equals(Column.TITLE)) {
            return report.getTitle();
        } else if (columns[columnIndex].equals(Column.ID)) {
            return report.getId();
        } else if (columns[columnIndex].equals(Column.DATETIME_GENERATED)) {
            return report.getDateTimeGenerated().toString("yyyy-MM-dd HH:mm:ss");
        } else if (columns[columnIndex].equals(Column.NOTE)) {
            return report.getNote();
        }
        
        throw new IllegalArgumentException("Unsupported column!");
    }
    
    
    private ResourceBundle strings = ResourcesManager.getStrings();

    public enum Column {

        ID, TITLE, DATETIME_GENERATED, NOTE
    };
    private List<ReportDTO> reports;
    private Column[] columns;
}
