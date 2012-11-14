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

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.key2gym.business.api.dtos.ClientDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ClientsTableModel extends AbstractTableModel {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

    public enum Column {ID, CARD, FULL_NAME};
    
    private List<ClientDTO> clients;
    private Column[] columns;

    public ClientsTableModel(Column[] columns) {
        this(columns, new LinkedList<ClientDTO>());
    }
    public ClientsTableModel(Column[] columns, List<ClientDTO> clients) {
        this.columns = columns;
        this.clients = clients;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clients = clients;
        fireTableDataChanged();
    }

    public List<ClientDTO> getClients() {
        return clients;
    }

    @Override
    public int getRowCount() {
        return clients.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columns[columnIndex].equals(Column.ID)) {
            return strings.getString("Text.ID");
        } else if (columns[columnIndex].equals(Column.CARD)) {
            return strings.getString("Text.Card");
        } else if (columns[columnIndex].equals(Column.FULL_NAME)) {
            return strings.getString("Text.FullName");
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
        ClientDTO clientDTO = clients.get(rowIndex);
        if (columns[columnIndex].equals(Column.ID)) {
            return clientDTO.getId().toString();
        } else if (columns[columnIndex].equals(Column.CARD)) {
            return clientDTO.getCard() == null ? "" : clientDTO.getCard().toString();
        } else if (columns[columnIndex].equals(Column.FULL_NAME)) {
            return clientDTO.getFullName();
        }
        throw new IllegalArgumentException();
    }
}