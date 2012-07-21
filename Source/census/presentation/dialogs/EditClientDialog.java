/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.dialogs;

import census.business.AttendancesService;
import census.business.ClientProfilesService;
import census.business.ClientsService;
import census.business.OrdersService;
import census.business.FreezesService;
import census.business.SessionsService;
import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.AttendanceDTO;
import census.business.dto.ClientProfileDTO;
import census.business.dto.OrderDTO;
import census.business.dto.ItemDTO;
import census.presentation.CensusFrame;
import census.presentation.util.AttendancesTableModel;
import census.presentation.util.FreezesTableModel;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.joda.time.DateMidnight;

/**
 * This dialog allows user to view and edit the client's information. Features
 * are:
 *
 * <ul>
 *
 * <li> Basic information </li> <li> Freezes </li> <li> Profile information
 * </li> <li> Previous attendances </li>
 *
 * </ul>
 *
 * Session variables:
 *
 * <ul>
 *
 * <li> clientId - the ID of the client to be shown and edited. </li>
 *
 * </ul>
 *
 * A transaction is required to be active, and a session to be open, upon the
 * dialog's creation.
 *
 * This dialog supports hot swapping. The session variables can be set and reset
 * after the
 * <code>setVisible(true)</code> was called.
 *
 * @author Danylo Vashchilenko
 */
public class EditClientDialog extends CensusDialog {

    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    /**
     * Creates a new instance of this class.
     *
     * The dialog will position itself in the center of its parent.
     *
     * @param parent the parent frame
     */
    public EditClientDialog(JFrame parent) {
        super(parent, true);
        sessionsService = SessionsService.getInstance();
        clientsService = ClientsService.getInstance();
        clientProfilesService = ClientProfilesService.getInstance();
        attendancesService = AttendancesService.getInstance();
        freezesService = FreezesService.getInstance();
        ordersService = OrdersService.getInstance();

        initComponents();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setLocationRelativeTo(getParent());
        }
        super.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        client = new census.business.dto.ClientDTO();
        clientProfile = new census.business.dto.ClientProfileDTO();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        clientTabPanel = new javax.swing.JPanel();
        clientPanel = new census.presentation.blocks.ClientPanel();
        profileTabPanel = new javax.swing.JPanel();
        clientProfilePanel = new census.presentation.blocks.ClientProfilePanel();
        attachedCheckBox = new javax.swing.JCheckBox();
        attendancesTableScrollPane = new javax.swing.JScrollPane();
        attendancesTable = new javax.swing.JTable();
        freezesTabPanel = new javax.swing.JPanel();
        freezesTableScrollPane = new javax.swing.JScrollPane();
        freezesTable = new javax.swing.JTable();
        freezeNoteScrollPane = new javax.swing.JScrollPane();
        freezeNoteTextArea = new javax.swing.JTextArea();
        ordersTabPanel = new javax.swing.JPanel();
        purchasesTreeScrollPane = new javax.swing.JScrollPane();
        purchasesTree = new javax.swing.JTree();
        purchasesFilterComboBox = new javax.swing.JComboBox();
        purchasesFilterLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        okButton.setText(bundle.getString("Button.Ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("Button.Cancel")); // NOI18N
        cancelButton.setActionCommand("null");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout clientTabPanelLayout = new javax.swing.GroupLayout(clientTabPanel);
        clientTabPanel.setLayout(clientTabPanelLayout);
        clientTabPanelLayout.setHorizontalGroup(
            clientTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
        );
        clientTabPanelLayout.setVerticalGroup(
            clientTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clientPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
        );

        tabbedPane.addTab(bundle.getString("Tab.Client"), clientTabPanel); // NOI18N

        attachedCheckBox.setText(bundle.getString("Text.Attached")); // NOI18N

        javax.swing.GroupLayout profileTabPanelLayout = new javax.swing.GroupLayout(profileTabPanel);
        profileTabPanel.setLayout(profileTabPanelLayout);
        profileTabPanelLayout.setHorizontalGroup(
            profileTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profileTabPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(attachedCheckBox)
                .addGap(138, 138, 138))
            .addComponent(clientProfilePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
        );
        profileTabPanelLayout.setVerticalGroup(
            profileTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profileTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attachedCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientProfilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        tabbedPane.addTab(bundle.getString("Text.Profile"), profileTabPanel); // NOI18N

        attendancesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        attendancesTableScrollPane.setViewportView(attendancesTable);

        tabbedPane.addTab(bundle.getString("Tab.Attendances"), attendancesTableScrollPane); // NOI18N

        FreezesTableModel.Column[] freezesTableColumns =
        new FreezesTableModel.Column[]{
            FreezesTableModel.Column.ADMINISTRATOR_FULL_NAME,
            FreezesTableModel.Column.DATE_ISSUED,
            FreezesTableModel.Column.DAYS,
            FreezesTableModel.Column.DATE_EXPIRED,
            FreezesTableModel.Column.NOTE
        };

        freezesTableModel = new FreezesTableModel(freezesTableColumns);
        freezesTable.setModel(freezesTableModel);

        int[] freezesTableColumnWidths = new int[]{94, 83, 41, 84, 88};
        TableColumn column = null;
        for (int i = 0; i < freezesTableColumnWidths.length; i++) {
            column = freezesTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(freezesTableColumnWidths[i]);
        }

        freezesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (freezesTable.getSelectedRowCount() > 1) {
                    freezeNoteTextArea.setText(" ... ");
                } else if (freezesTable.getSelectedRowCount() < 1) {
                    freezeNoteTextArea.setText(null);
                } else {
                    freezeNoteTextArea.setText(freezesTableModel.getFreezeAt(freezesTable.getSelectedRow()).getNote());
                }
            }
        });
        freezesTableScrollPane.setViewportView(freezesTable);

        freezeNoteScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("Text.Note"))); // NOI18N

        freezeNoteTextArea.setColumns(20);
        freezeNoteTextArea.setRows(5);
        freezeNoteTextArea.setEnabled(false);
        freezeNoteScrollPane.setViewportView(freezeNoteTextArea);

        javax.swing.GroupLayout freezesTabPanelLayout = new javax.swing.GroupLayout(freezesTabPanel);
        freezesTabPanel.setLayout(freezesTabPanelLayout);
        freezesTabPanelLayout.setHorizontalGroup(
            freezesTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(freezesTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(freezesTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(freezesTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(freezeNoteScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                .addContainerGap())
        );
        freezesTabPanelLayout.setVerticalGroup(
            freezesTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(freezesTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(freezesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(freezeNoteScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("Tab.Freezes"), freezesTabPanel); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        purchasesTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        purchasesTree.setRootVisible(false);
        purchasesTreeScrollPane.setViewportView(purchasesTree);

        String[] periods = new String[]{
            bundle.getString("Text.Last7Days"),
            bundle.getString("Text.LastMonth"),
            bundle.getString("Text.Last3Months"),
            bundle.getString("Text.All")
        };
        purchasesFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel(periods));
        purchasesFilterComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                purchasesFilterComboBoxItemStateChanged(evt);
            }
        });

        purchasesFilterLabel.setText(bundle.getString("Label.Filter")); // NOI18N

        javax.swing.GroupLayout ordersTabPanelLayout = new javax.swing.GroupLayout(ordersTabPanel);
        ordersTabPanel.setLayout(ordersTabPanelLayout);
        ordersTabPanelLayout.setHorizontalGroup(
            ordersTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(purchasesTreeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
            .addGroup(ordersTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(purchasesFilterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(purchasesFilterComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ordersTabPanelLayout.setVerticalGroup(
            ordersTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ordersTabPanelLayout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(ordersTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(purchasesFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(purchasesFilterLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(purchasesTreeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPane.addTab(bundle.getString("Tab.Orders"), ordersTabPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Processes an OK button click event.
     *
     * @param evt an optional action event
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        /*
         * clientPanel is required to be valid, while clientProfilePanel has to
         * be valid only if it is (or is going to be) attached.
         */
        if (!clientPanel.isFormValid() || (attachedCheckBox.isSelected() && !clientProfilePanel.isFormValid())) {
            return;
        }

        try {
            /*
             * Updates the client
             */
            clientsService.updateClient(client, sessionsService.getPermissionsLevel().equals(SessionsService.PL_ALL));

            /*
             * Attaches (and also updates) or removes the profile
             */
            if (attachedCheckBox.isSelected()) {
                ClientProfilesService.getInstance().attachClientProfile(clientProfile);
            } else if (clientProfileAttached) {
                ClientProfilesService.getInstance().detachClientProfile(clientProfile.getId());
            }
        } catch (SecurityException ex) {
            /*
             * GUI garantess that restricted operations can not be permored, so
             * this is probably a bug.
             */
            setResult(EditOrderDialog.RESULT_EXCEPTION);
            setException(new RuntimeException(ex));
            dispose();
            return;
        } catch (BusinessException | ValidationException ex) {
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(EditOrderDialog.RESULT_EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setResult(RESULT_OK);
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Processes a Cancel button click event.
     *
     * @param evt an optional action event
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setResult(RESULT_CANCEL);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void purchasesFilterComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_purchasesFilterComboBoxItemStateChanged
        reloadPurchasesTab();
    }//GEN-LAST:event_purchasesFilterComboBoxItemStateChanged

    /**
     * Gets the client's ID.
     *
     * The client's ID is the ID that was set with
     * <code>setClientId</code>.
     *
     * @return the client's ID
     */
    public Short getClientId() {
        return clientId;
    }

    /**
     * Sets the client's ID. This method causes all components to be reloaded in
     * order to correspond with the new client.
     *
     * @param clientId the client's ID
     * @see EditClientDialog for details about hot swapping
     */
    public void setClientId(Short clientId) {
        this.clientId = clientId;

        try {
            client = clientsService.getById(getClientId());
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }

        try {
            clientProfile = clientProfilesService.getById(clientId);
            clientProfileAttached = true;
        } catch (ValidationException ex) {
            clientProfile = new ClientProfileDTO();
            clientProfileAttached = false;
        }

        /*
         * Title
         */
        setTitle(MessageFormat.format(bundle.getString("Title.Client.withFullName"), new Object[]{client.getFullName()}));

        /*
         * Client tab
         */
        clientPanel.setClient(client);

        /*
         * Profile tab
         */
        clientProfilePanel.setClientProfile(clientProfile);
        attachedCheckBox.setSelected(clientProfileAttached);
        // The administrator needs to have PL_ALL permissions level to detach a profile
        attachedCheckBox.setEnabled(!clientProfileAttached || sessionsService.getPermissionsLevel().equals(census.business.SessionsService.PL_ALL));

        /*
         * Attendances tab
         */
        TableModel attendancesTableModel;

        // The administrator needs to have PL_ALL permissions level to view old attendances
        if (sessionsService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            AttendancesTableModel.Column[] attendancesTableColumns =
                    new AttendancesTableModel.Column[]{
                AttendancesTableModel.Column.BEGIN_DATE,
                AttendancesTableModel.Column.BEGIN,
                AttendancesTableModel.Column.KEY,
                AttendancesTableModel.Column.END
            };
            List<AttendanceDTO> attendances;
            try {
                attendances = attendancesService.findAttendancesByClient(client.getId());
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }
            attendancesTableModel = new AttendancesTableModel(attendancesTableColumns, attendances);
            attendancesTable.setModel(attendancesTableModel);
        } else {
            tabbedPane.setComponentAt(tabbedPane.indexOfComponent(attendancesTableScrollPane), new JLabel(bundle.getString("Message.AccessIsDenied"), JLabel.CENTER));
        }

        /*
         * Freezes tab
         */
        try {
            freezesTableModel.setFreezes(freezesService.findFreezesForClient(clientId));
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }

        /*
         * Purchases tab
         */
        reloadPurchasesTab();

    }

    private void reloadPurchasesTab() {
        /*
         * Purchases tab
         */
        List<OrderDTO> ordersDTO;
        DateMidnight end = new DateMidnight();
        DateMidnight begin;
        if (purchasesFilterComboBox.getSelectedIndex() == 0) {
            begin = end.minusDays(7);
        } else if (purchasesFilterComboBox.getSelectedIndex() == 1) {
            begin = end.minusMonths(1);
        } else if (purchasesFilterComboBox.getSelectedIndex() == 2) {
            begin = end.minusMonths(3);
        } else {
            begin = client.getRegistrationDate();
        }

        try {
            ordersDTO = ordersService.findForClientWithinPeriod(clientId, begin, end);
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }

        DefaultMutableTreeNode topNode = new DefaultMutableTreeNode();
        DefaultMutableTreeNode dateNode;
        DefaultMutableTreeNode itemNode;
        for (OrderDTO orderDTO : ordersDTO) {
            String text = MessageFormat.format(bundle.getString("Text.Order.withDateAndTotalAndPaid"),
                    new Object[]{orderDTO.getDate().toString("dd-MM-yyyy"), //NOI18N
                        orderDTO.getTotal().toPlainString(),
                        orderDTO.getPayment().toPlainString()
                    });
            dateNode = new DefaultMutableTreeNode(text);

            for (ItemDTO item : orderDTO.getItems()) {
                itemNode = new DefaultMutableTreeNode(item.getTitle());
                dateNode.add(itemNode);
            }

            topNode.add(dateNode);
        }

        purchasesTree.setModel(new DefaultTreeModel(topNode));
    }
    /*
     * Business services
     */
    private SessionsService sessionsService;
    private ClientsService clientsService;
    private ClientProfilesService clientProfilesService;
    private AttendancesService attendancesService;
    private FreezesService freezesService;
    private OrdersService ordersService;
    /*
     * GUI variables
     */
    private Boolean clientProfileAttached;
    private FreezesTableModel freezesTableModel;
    /*
     * Session variables
     */
    private Short clientId;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox attachedCheckBox;
    private javax.swing.JTable attendancesTable;
    private javax.swing.JScrollPane attendancesTableScrollPane;
    private javax.swing.JButton cancelButton;
    private census.business.dto.ClientDTO client;
    private census.presentation.blocks.ClientPanel clientPanel;
    private census.business.dto.ClientProfileDTO clientProfile;
    private census.presentation.blocks.ClientProfilePanel clientProfilePanel;
    private javax.swing.JPanel clientTabPanel;
    private javax.swing.JScrollPane freezeNoteScrollPane;
    private javax.swing.JTextArea freezeNoteTextArea;
    private javax.swing.JPanel freezesTabPanel;
    private javax.swing.JTable freezesTable;
    private javax.swing.JScrollPane freezesTableScrollPane;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel ordersTabPanel;
    private javax.swing.JPanel profileTabPanel;
    private javax.swing.JComboBox purchasesFilterComboBox;
    private javax.swing.JLabel purchasesFilterLabel;
    private javax.swing.JTree purchasesTree;
    private javax.swing.JScrollPane purchasesTreeScrollPane;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
