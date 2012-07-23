/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.dto.ClientDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ClientsTableModel extends AbstractTableModel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

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
            return bundle.getString("Text.ID");
        } else if (columns[columnIndex].equals(Column.CARD)) {
            return bundle.getString("Text.Card");
        } else if (columns[columnIndex].equals(Column.FULL_NAME)) {
            return bundle.getString("Text.FullName");
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