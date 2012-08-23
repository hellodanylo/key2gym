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
package census.presentation.dialogs;

import census.business.*;
import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.business.api.SecurityException;
import census.business.dto.*;
import census.presentation.util.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.*;

/**
 * This dialog displays and edits an order. <p> Session variables: <ul> <li>
 * orderId - the ID of order to be shown and edited </li> <li> fullPaymentForced
 * - if true, the dialog won't exit with RESULT_OK, if the user did not record
 * full payment </li> </ul> </p> <p> A transaction is required to be active, and
 * a session to be open, upon the dialog's creation. </p> <p> This dialog
 * supports hot swapping. The session variables can be set and reset after the
 * <code>setVisible(true)</code> was called. </p>
 *
 * @author Danylo Vashchilenko
 */
public class EditOrderDialog extends AbstractDialog {

    /**
     * Constructs from a parent frame.
     *
     * @param parent the frame to use when positioning itself
     */
    public EditOrderDialog(JFrame parent) {
        super(parent, true);

        ordersService = OrdersService.getInstance();
        clientsService = ClientsService.getInstance();
        attendancesService = AttendancesService.getInstance();
        itemsService = ItemsService.getInstance();
        fullPaymentForced = false;

        order = new OrderDTO();

        buildDialog();
    }

    private void buildDialog() {

        setLayout(new FormLayout("4dlu, [200dlu, d]:g, 4dlu, [150dlu, d]:g, 4dlu, d", "4dlu, f:d:g, 4dlu, f:d, 4dlu, d, 4dlu"));

        add(createBasicPanel(), CC.xy(2, 4));
        add(createOrderLinesPanel(), CC.xywh(2, 2, 3, 1));
        add(createPaymentPanel(), CC.xy(4, 4));
        add(createNewOrderLinePanel(), CC.xywh(6, 2, 1, 3));

        add(createButtonsPanel(), CC.xywh(2, 6, 5, 1));


        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getString("Title.Order")); // NOI18N
        getRootPane().setDefaultButton(okButton);
        setMinimumSize(getPreferredSize());
        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createBasicPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(getString("Text.BasicInformation"))); // NOI18N

        FormLayout layout = new FormLayout("r:d, 3dlu, d:g", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, getStrings(), panel);

        subjectTextField = new JTextField();
        subjectTextField.setEditable(false);
        builder.appendI15d("Label.Subject", subjectTextField);

        dateTextField = new JTextField();
        dateTextField.setEditable(false);
        builder.appendI15d("Label.Date", dateTextField);

        moneyBalanceTextField = new JTextField();
        moneyBalanceTextField.setEditable(false);
        //moneyBalanceTextField.setFont(new Font("DejaVu Sans", 0, 18)); // NOI18N
        builder.appendI15d("Label.MoneyBalance", moneyBalanceTextField);

        return panel;
    }

    private JPanel createOrderLinesPanel() {
        JPanel panel = new JPanel(new FormLayout("d:g, d", "b:d:g, t:d:g"));

        OrderLinesTableModel.Column[] orderLinesTableColumns = new OrderLinesTableModel.Column[]{
            OrderLinesTableModel.Column.ITEM_TITLE,
            OrderLinesTableModel.Column.ITEM_PRICE,
            OrderLinesTableModel.Column.QUANTITY,
            OrderLinesTableModel.Column.DISCOUNT_TITLE,
            OrderLinesTableModel.Column.TOTAL
        };
        orderLinesTableModel = new OrderLinesTableModel(orderLinesTableColumns);

        orderLinesTable = new JTable();
        orderLinesTable.setModel(orderLinesTableModel);
        orderLinesTable.getColumnModel().getColumn(0).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(orderLinesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(getString("Text.OrderLines")));
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, CC.xywh(1, 1, 1, 2));

        panel.add(new JButton(createIncreaseQuantityAction()), CC.xy(2, 1));
        panel.add(new JButton(createDecreaseQuantityAction()), CC.xy(2, 2));
        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(getString("Text.Payment"))); // NOI18N

        FormLayout layout = new FormLayout("r:d, 3dlu, d:g");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, getStrings(), panel);

        totalTextField = new JTextField();
        totalTextField.setEditable(false);
        totalTextField.setFont(new Font("DejaVu Sans", 0, 18)); // NOI18N
        totalTextField.setHorizontalAlignment(JTextField.LEFT);
        builder.appendI15d("Label.Total", totalTextField); // NOI18N

        paidTextField = new JTextField();
        paidTextField.setEditable(false);
        paidTextField.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        paidTextField.setHorizontalAlignment(JTextField.LEFT);
        builder.appendI15d("Label.Paid", paidTextField); // NOI18N

        dueTextField = new JTextField();
        dueTextField.setEditable(false);
        dueTextField.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        dueTextField.setHorizontalAlignment(JTextField.LEFT);
        builder.appendI15d("Label.Due", dueTextField); // NOI18N

        paymentTextField = new JTextField();
        paymentTextField.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        paymentTextField.setHorizontalAlignment(JTextField.LEFT);
        paymentTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent evt) {
                paymentTextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(FocusEvent evt) {
                paymentTextFieldFocusLost(evt);
            }
        });
        builder.appendI15d("Label.Payment", paymentTextField); // NOI18N

        return panel;
    }

    private JPanel createNewOrderLinePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(getString("Title.AddOrderLine")));

        FormLayout layout = new FormLayout("r:d, 3dlu, d:g", "f:d:g");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, getStrings(), panel);

        itemsList = new JList();

        itemsListModel = new MutableListModel<>();
        itemsList.setModel(itemsListModel);

        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsList.setCellRenderer(new ItemListCellRenderer());

        JScrollPane itemsScrollPane = new JScrollPane();
        itemsScrollPane.setViewportView(itemsList);

        builder.append(itemsScrollPane, 3);
        builder.nextLine();

        List<DiscountDTO> discounts = DiscountsService.getInstance().findAll();
        discounts.add(0, null);
        discountsComboBox = new JComboBox();
        discountsComboBox.setModel(new DefaultComboBoxModel(discounts.toArray()));
        discountsComboBox.setRenderer(new DiscountListCellRenderer());

        builder.appendI15d("Label.Discount", discountsComboBox);
        builder.nextLine();

        quantitySpinner = new JSpinner();
        quantitySpinner.setModel(new SpinnerNumberModel(1, 1, 10, 1));

        builder.appendI15d("Label.Quantity", quantitySpinner);
        builder.nextLine();

        builder.append(new JButton(createAddOrderLineAction()), 3);

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());
        okButton.setPreferredSize(cancelButton.getPreferredSize());

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }

    private void updateGUI(boolean softReset) {
        try {
            order = ordersService.getById(orderId);
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }

        /*
         * Total
         */
        totalTextField.setText(order.getTotal().toPlainString());

        /*
         * Paid
         */
        paidTextField.setText(order.getPayment().toPlainString());

        /*
         * Due
         */
        dueTextField.setForeground(order.getDue().compareTo(BigDecimal.ZERO) > 0 ?  ColorConstants.ERROR_FOREGROUND: ColorConstants.OK_FOREGROUND);
        dueTextField.setBackground(order.getDue().compareTo(BigDecimal.ZERO) > 0 ?  ColorConstants.ERROR_BACKGROUND : ColorConstants.OK_BACKGROUND);
        dueTextField.setText(order.getDue().toPlainString());

        /*
         * Payment
         */
        if (!softReset) {
            paymentTextField.setText("0.00"); //NOI18N
        }

        /*
         * Items list. It has to be reloaded for some items could have gone out
         * of stock since last update. However, we want to preserve the selected
         * item for convinience.
         */
        List<ItemDTO> items;
        int index = itemsList.getSelectedIndex();
        if (order.getClientId() == null) {
            items = itemsService.getPureItemsAvailable();
        } else {
            items = itemsService.getItemsAvailable();
        }
        itemsListModel.set(items);
        if (index >= items.size()) {
            index = items.size() - 1;
        }
        itemsList.setSelectedIndex(index);

        /*
         * Purchases table. It has to be reloaded for some items could have been
         * bought or returned since last update. However, we want to preserve
         * the selected item for convinience.
         */
        List<OrderLineDTO> orderLines = order.getOrderLines();
        index = orderLinesTable.getSelectedRow();
        orderLinesTableModel.setOrderLines(orderLines);
        
        if (index >= orderLines.size()) {
            index = orderLines.size() - 1;
        } else if(index == -1 && orderLines.size() > 0) {
            index = 0;
        }
        
        orderLinesTable.getSelectionModel().setSelectionInterval(index, index);

        /*
         * Money balance
         */
        if (order.getMoneyBalance() == null) {
            moneyBalanceTextField.setEnabled(false);
        } else {
            moneyBalanceTextField.setEnabled(true);
            moneyBalanceTextField.setText(order.getMoneyBalance().toPlainString());
            moneyBalanceTextField.setForeground(order.getMoneyBalance().compareTo(BigDecimal.ZERO) < 0 ? new Color(168, 0, 0) : new Color(98, 179, 0));
            moneyBalanceTextField.setBackground(order.getMoneyBalance().compareTo(BigDecimal.ZERO) < 0 ? new Color(255, 173, 206) : new Color(211, 255, 130));
        }
        /*
         * We update basic information only upon hard resets for it could not
         * have changed since last update.
         */
        if (!softReset) {
            String subject;
            if (order.getClientId() == null) {
                if (order.getAttendanceId() != null) {
                    AttendanceDTO attendance;
                    try {
                        attendance = attendancesService.getAttendanceById(order.getAttendanceId());
                    } catch (SecurityException ex) {
                        throw new RuntimeException(ex);
                    }
                    subject = MessageFormat.format(getString("Text.Attendance.withIDAndKey"),
                            new Object[]{
                                attendance.getId(),
                                attendance.getKeyTitle()
                            });
                } else {
                    subject = getString("Text.Other");
                }
            } else {
                ClientDTO client;
                try {
                    client = clientsService.getById(order.getClientId());
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }
                subject = MessageFormat.format(getString("Text.Client.withFullNameAndID"),
                        new Object[]{
                            client.getFullName(),
                            client.getId()
                        });
            }
            subjectTextField.setText(subject);
            subjectTextField.getCaret().setDot(0);

            /*
             * Date
             */
            dateTextField.setText(MessageFormat.format("{0, date, long}", order.getDate().toDate())); //NOI18N
        }
    }

    /**
     * Returns a new instance of increase quantity action. <p> This action is
     * used to increase the quantity of the currently selected order line.
     *
     * @return the action
     */
    private Action createIncreaseQuantityAction() {

        return new AbstractAction() {

            {
                putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/census/presentation/resources/plus16.png")));
            }

            @Override
            public void actionPerformed(ActionEvent evt) {
                OrderLineDTO orderLine;

                int selectedIndex = orderLinesTable.getSelectedRow();

                if (selectedIndex == -1) {
                    return;
                }

                orderLine = order.getOrderLines().get(selectedIndex);

                try {
                    ordersService.addPurchase(order.getId(), orderLine.getItemId(), orderLine.getDiscountId());
                } catch (BusinessException | SecurityException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                } catch (ValidationException | RuntimeException ex) {
                    /*
                     * The exception is unexpected. We got to shutdown the
                     * dialog for the state of the transaction is now unknown.
                     */
                    setResult(RESULT_EXCEPTION);
                    setException(new RuntimeException(ex));
                    dispose();
                    return;
                }

                updateGUI(true);
            }
        };
    }

    /**
     * Returns a new instance of decrease quantity action. <p> This action is
     * used to decrease the quantity of the currently selected order line.
     *
     * @return the action
     */
    private Action createDecreaseQuantityAction() {

        return new AbstractAction() {

            {
                putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/census/presentation/resources/remove16.png")));
            }

            @Override
            public void actionPerformed(ActionEvent evt) {


                int orderLineIndex = orderLinesTable.getSelectionModel().getMinSelectionIndex();

                if (orderLineIndex == -1) {
                    ValidationException ex = new ValidationException(getString("Message.SelectOrderLineFirst"));
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                }

                OrderLineDTO orderLine = order.getOrderLines().get(orderLineIndex);

                try {
                    ordersService.removePurchase(orderLine.getId());
                } catch (BusinessException | SecurityException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                } catch (ValidationException | RuntimeException ex) {
                    /*
                     * The exception is unexpected. We got to shutdown the
                     * dialog for the state of the transaction is now unknown.
                     */
                    setResult(RESULT_EXCEPTION);
                    setException(new RuntimeException(ex));
                    dispose();
                    return;
                }

                // Reloads the order and updates GUI
                setOrderId(orderId);
            }
        };
    }

    /**
     * Returns a new instance of add order line action. <p> This action is used
     * to add new order line to the order.
     *
     * @return the action
     */
    private Action createAddOrderLineAction() {
        return new AbstractAction() {

            {
                putValue(NAME, getString("Button.Add"));
                putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/census/presentation/resources/plus16.png"))); 
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemsList.getSelectedValue() == null) {
                    /*
                     * Asks the user to select an item.
                     */
                    ValidationException ex = new ValidationException(getString("Message.SelectItemFirst"));
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                }

                Short itemId = ((ItemDTO) itemsList.getSelectedValue()).getId();

                try {
                    DiscountDTO discount = (DiscountDTO) discountsComboBox.getSelectedItem();
                    for (int i = 0; i < (Integer) quantitySpinner.getValue(); i++) {
                        ordersService.addPurchase(order.getId(), itemId, discount == null ? null : discount.getId());
                    }
                } catch (BusinessException | ValidationException | SecurityException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                } catch (RuntimeException ex) {
                    /*
                     * The exception is unexpected. We got to shutdown the
                     * dialog for the state of the transaction is now unknown.
                     */
                    setResult(RESULT_EXCEPTION);
                    setException(ex);
                    dispose();
                    return;
                }

                // We do a GUI update with soft reset to reload data that might have changed.
                updateGUI(true);
            }
        };
    }

    /**
     * Processes an OK button click.
     *
     * @param evt an optional event
     */
    @Override
    protected void onOkActionPerformed(ActionEvent evt) {

        try {
            BigDecimal newPayment = new BigDecimal(paymentTextField.getText().trim());

            if (isFullPaymentForced() && order.getDue().compareTo(newPayment) != 0) {
                throw new BusinessException(getString("Message.FullPaymenIsForced"));
            }

            ordersService.recordPayment(order.getId(), newPayment);
        } catch (NumberFormatException ex) {
            String message = MessageFormat.format(getString("Message.FieldIsNotFilledInCorrectly.withFieldName"),
                    getString("Text.NewPayment"));
            UserExceptionHandler.getInstance().processException(new ValidationException(message));
            return;
        } catch (BusinessException | ValidationException | SecurityException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        } catch (RuntimeException ex) {
            /*
             * The exception is unexpected. We got to shutdown the dialog for
             * the state of the transaction is now unknown.
             */
            setResult(RESULT_EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        setResult(RESULT_OK);
        dispose();
    }

    private void paymentTextFieldFocusGained(FocusEvent evt) {
        paymentTextField.setSelectionStart(0);
        paymentTextField.setSelectionEnd(paymentTextField.getDocument().getLength());
    }

    private void paymentTextFieldFocusLost(FocusEvent evt) {
        paymentTextField.setSelectionEnd(0);
        paymentTextField.setSelectionEnd(0);
    }

    /**
     * Sets the order's ID. This method causes all components to be reloaded in
     * order to correspond with the new order.
     *
     * @param orderId the
     * @see EditOrderDialog for details about hot swapping
     */
    public void setOrderId(Short orderId) {
        this.orderId = orderId;

        updateGUI(false);
    }

    public Boolean isFullPaymentForced() {
        return fullPaymentForced;
    }

    public void setFullPaymentForced(Boolean fullPaymentForced) {
        this.fullPaymentForced = fullPaymentForced;
    }

    public Short getOrderId() {
        return orderId;
    }
    /*
     * Presentation
     */
    private OrderLinesTableModel orderLinesTableModel;
    private MutableListModel<ItemDTO> itemsListModel;
    private Short orderId;
    private Boolean fullPaymentForced;
    /*
     * Business
     */
    private OrdersService ordersService;
    private ClientsService clientsService;
    private AttendancesService attendancesService;
    private ItemsService itemsService;
    /*
     * Components
     */
    private JTextField dateTextField;
    private JTextField moneyBalanceTextField;
    private JComboBox discountsComboBox;
    private JTextField dueTextField;
    private JList itemsList;
    private JButton okButton;
    private JButton cancelButton;
    private OrderDTO order;
    private JTable orderLinesTable;
    private JTextField paidTextField;
    private JTextField paymentTextField;
    private JTextField subjectTextField;
    private JTextField totalTextField;
    private JSpinner quantitySpinner;
}
