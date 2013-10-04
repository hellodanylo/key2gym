package org.key2gym.client.panels.forms;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.dtos.Debtor;
import org.key2gym.business.api.services.ClientsService;
import org.key2gym.client.Main;
import org.key2gym.client.dialogs.EditClientDialog;
import org.key2gym.client.resources.ResourcesManager;
import org.key2gym.client.util.DebtorsTableModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 10/2/13
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class DebtorsPanel extends JPanel {

    private ClientsService clientsService;
    private List<Debtor> debtorsList;
    private JTable debtorsTable;
    private JScrollPane debtorsScrollPane;
    private JTextField debtorsCountTextField;
    private DebtorsTableModel debtorsTableModel;
    
    public DebtorsPanel() throws SecurityViolationException {
        this.clientsService = Main.getContext().getBean(ClientsService.class);
        this.debtorsList = this.clientsService.findDebtors();

        initComponents();
        buildPanel();
    }

    /**
     * Initializes the panel's components.
     */
    private void initComponents() {

        /*
         * Debtors journal
         */
        debtorsScrollPane = new JScrollPane();
        debtorsTable = new JTable();

        debtorsTableModel = new DebtorsTableModel(debtorsList);
        debtorsTable.setModel(debtorsTableModel);
        debtorsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        debtorsScrollPane.setViewportView(debtorsTable);

        /*
         * Opens the selected attendance's client on double left-click.
         */
        debtorsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Integer clientId = getSelectedDebtor().getClientId();

                EditClientDialog dialog = null;
                try {
                    dialog = new EditClientDialog(null);
                } catch (SecurityViolationException ex) {
                    throw new RuntimeException("Unexpected security violation", ex);
                }
                dialog.setClientId(clientId);
                dialog.setVisible(true);
            }
        });

        /*
         * Debtors counter
         */
        debtorsCountTextField = new JTextField();
        debtorsCountTextField.setEditable(false);
        debtorsCountTextField.setColumns(8);
        debtorsCountTextField.setText(String.valueOf(this.debtorsList.size()));
    }

    /**
     * Builds the panel by placing components on it.
     */
    private void buildPanel() {
        FormLayout layout = new FormLayout(
                "5dlu, default, 3dlu, fill:default, default:grow",
                "5dlu, default, 3dlu, fill:default:grow");
        setLayout(layout);

        add(new JLabel(ResourcesManager.getString("Label.Debtors")), CC.xy(2, 2));
        add(debtorsCountTextField, CC.xy(4, 2));
        add(debtorsScrollPane, CC.xywh(1, 4, 5, 1));
    }

    /**
     * Returns currently selected attendance.
     *
     * @return the selected attendance or null, if none is selected
     */
    public Debtor getSelectedDebtor() {
        int index = debtorsTable.getSelectedRow();
        if (index == -1) {
            return null;
        }
        return debtorsTableModel.getDebtorAt(index);
    }
}
