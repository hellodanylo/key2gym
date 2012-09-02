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
package org.key2gym.presentation.panels;

import org.key2gym.business.CashService;
import org.key2gym.business.OrdersService;
import org.key2gym.business.SessionsService;
import org.key2gym.business.StorageService;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.dto.OrderDTO;
import org.key2gym.presentation.actions.BasicAction;
import org.key2gym.presentation.actions.EditOrderAction;
import org.key2gym.presentation.renderers.OrdersTableCellRenderer;
import org.key2gym.presentation.util.OrdersTableModel;
import org.key2gym.presentation.util.OrdersTableModel.Column;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OrdersPanel extends javax.swing.JPanel {

    /**
     * Creates new form OrdersPanel
     */
    public OrdersPanel() {
        financialActivitiesService = OrdersService.getInstance();
        cashService = CashService.getInstance();
        strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");

        initComponents();
        buildPanel();

        observer = new CustomObserver();
    }

    private void initComponents() {

        cashTextField = new JTextField();
        cashTextField.setEditable(false);
        cashTextField.setColumns(8);
        
        totalTextField = new JTextField();
        totalTextField.setEditable(false);
        totalTextField.setColumns(8);

        ordersTableScrollPane = new JScrollPane();
        ordersTable = new JTable();

        /*
         * This renderer highlights orders with payment due
         */
        ordersTable.setDefaultRenderer(String.class, new OrdersTableCellRenderer());

        /*
         * Columns of the orders table
         */
        Column[] сolumns =
                new Column[]{
            Column.ID,
            Column.SUBJECT,
            Column.TOTAL,
            Column.PAID
        };

        financialActivitiesTableModel = new OrdersTableModel(сolumns);
        ordersTable.setModel(financialActivitiesTableModel);

        /*
         * Sets appropriate sizes of the columns.
         */
        int[] widths = new int[]{50, 200, 50, 50};
        TableColumn column;
        for (int i = 0; i < widths.length; i++) {
            column = ordersTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }
        ordersTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ordersTable.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                financialActivitiesTableFocusLost(evt);
            }
        });
        ordersTableScrollPane.setViewportView(ordersTable);

        /*
         * Opens the selected order on double left-click.
         */
        ordersTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
                    new EditOrderAction().actionPerformed(new ActionEvent(this, 0, BasicAction.ACTION_CONTEXT));
                }
            }
        });
    }

    private void buildPanel() {
        FormLayout layout = new FormLayout("5dlu, default, 3dlu, fill:default, 3dlu, default, 3dlu, fill:default, default:grow",
                "5dlu, default, 3dlu, fill:default:grow");
        setLayout(layout);

        add(new JLabel(strings.getString("Label.Total")), CC.xy(2, 2));
        add(totalTextField, CC.xy(4, 2));
        add(new JLabel(strings.getString("Label.Cash")), CC.xy(6, 2));
        add(cashTextField, CC.xy(8, 2));
        
        add(ordersTableScrollPane, CC.xywh(1, 4, 9, 1));
    }

    /**
     * Called when the orders table looses the focus.
     *
     * @param evt the focus event
     */
    private void financialActivitiesTableFocusLost(FocusEvent evt) {
        ordersTable.clearSelection();
    }

    private void updatePanel() throws SecurityException {
        List<OrderDTO> financialActivities = financialActivitiesService.findAllByDate(date);
        financialActivitiesTableModel.setFinancialActivities(financialActivities);

        BigDecimal total = financialActivitiesService.getTotalForDate(date);
        BigDecimal cash = cashService.getCashByDate(date);

        totalTextField.setText(total.toPlainString());
        cashTextField.setText(cash.toPlainString());
    }

    public OrderDTO getSelectedOrder() {
        int index = ordersTable.getSelectedRow();

        if (index == -1) {
            return null;
        }

        return financialActivitiesTableModel.getOrderAt(index);
    }

    public void setDate(DateMidnight date) throws SecurityException {
        this.date = date;
        updatePanel();
    }

    public DateMidnight getDate() {
        return date;
    }

    /*
     * Business
     */
    private OrdersService financialActivitiesService;
    private CashService cashService;

    /*
     * Presentation
     */
    private ResourceBundle strings;
    private DateMidnight date;
    private OrdersTableModel financialActivitiesTableModel;
    private CustomObserver observer;
    private JTextField cashTextField;
    private JTable ordersTable;
    private JScrollPane ordersTableScrollPane;
    private JTextField totalTextField;


    private class CustomObserver implements Observer {

        public CustomObserver() {
            registerSelf();
        }

        private void registerSelf() {
            StorageService.getInstance().addObserver(this);
        }

        @Override
        public void update(Observable o, Object arg) {
            if (SessionsService.getInstance().hasOpenSession()) {
                try {
                    updatePanel();
                } catch (SecurityException ex) {
                    Logger.getLogger(this.getClass().getName()).error("Unexpected SecurityException", ex);
                }
            }
        }
    }
    
}
