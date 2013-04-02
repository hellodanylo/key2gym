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
package org.key2gym.client;

import org.key2gym.client.actions.RegisterClientAction;
import org.key2gym.client.actions.ManageCashAction;
import org.key2gym.client.actions.OpenItemsWindowAction;
import org.key2gym.client.actions.FreezeClientAction;
import org.key2gym.client.actions.CheckInAction;
import org.key2gym.client.actions.OpenAttendancesWindowAction;
import org.key2gym.client.actions.OpenOrdersWindowAction;
import org.key2gym.client.actions.ToggleShadowSessionAction;
import org.key2gym.client.actions.ManageItemsAction;
import org.key2gym.client.actions.ManageKeysAction;
import org.key2gym.client.actions.AboutAction;
import org.key2gym.client.actions.ManageFreezesAction;
import org.key2gym.client.actions.ToggleSessionAction;
import org.key2gym.client.actions.CheckOutAction;
import org.key2gym.client.actions.EditOrderAction;
import org.key2gym.client.actions.ManageSubscriptionsAction;
import org.key2gym.client.actions.EditClientAction;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.*;
import org.key2gym.business.api.remote.*;
import org.key2gym.business.api.dtos.*;
import org.key2gym.client.actions.ManageReportsAction;
import org.key2gym.client.panels.AttendancesPanel;
import org.key2gym.client.panels.CloseableTabPanel;
import org.key2gym.client.panels.ItemsPanel;
import org.key2gym.client.panels.OrdersPanel;
import org.key2gym.client.util.ExceptionCatchingEventQueue;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MainFrame extends JFrame {

    /**
     * Creates new MainFrame
     */
    protected MainFrame() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new ExceptionCatchingEventQueue());

        attendancesPanels = new HashMap<>();
        ordersPanels = new HashMap<>();

        strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

        initComponents();

        ContextManager.getInstance().addObserver(new ContextManagerObserver());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing(e);
            }
        });
    }

    private void initComponents() {

        initActions();
        buildMenu();

        add(createActionsToolBar(), BorderLayout.NORTH);
        add(createFooterPanel(), BorderLayout.SOUTH);

        workspacesTabbedPane = new JTabbedPane();
        workspacesTabbedPane.setPreferredSize(new Dimension(800, 400));
        add(workspacesTabbedPane, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(strings.getString("Title.Key2Gym")); // NOI18N
        pack();
        setLocationRelativeTo(null);
    }

    private void initActions() {
        toggleRaisedAdministratorAction = new ToggleShadowSessionAction();
        freezeClientAction = new FreezeClientAction();
        manageCashAction = new ManageCashAction();
        manageFreezesAction = new ManageFreezesAction();
        editOrderAction = new EditOrderAction();
        openAttendancesWindowAction = new OpenAttendancesWindowAction();
        openOrdersWindowAction = new OpenOrdersWindowAction();
        editClientAction = new EditClientAction();
        checkOutAction = new CheckOutAction();
        checkInAction = new CheckInAction();
        registerClientAction = new RegisterClientAction();
        manageItemsAction = new ManageItemsAction();
        manageReportsAction = new ManageReportsAction();
        manageSubscriptionsAction = new ManageSubscriptionsAction();
        manageKeysAction = new ManageKeysAction();
        toggleSessionAction = new ToggleSessionAction();
        openItemsWindowAction = new OpenItemsWindowAction();
        aboutAction = new AboutAction();
    }

    private JToolBar createActionsToolBar() {

        JToolBar actionsToolBar = new JToolBar();
        JButton openAttendanceButton = new JButton();
        JButton closeAttendanceButton = new JButton();
        JToolBar.Separator attendancesClientsSeparator = new JToolBar.Separator();
        JButton registerClientButton = new JButton();
        JButton editClientButton = new JButton();
        JToolBar.Separator clientsFinancesSeparator = new JToolBar.Separator();
        JButton editOrderButton = new JButton();

        actionsToolBar.setFloatable(false);
        actionsToolBar.setRollover(true);

        openAttendanceButton.setAction(checkInAction);
        openAttendanceButton.setFocusable(false);
        openAttendanceButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openAttendanceButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        openAttendanceButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        actionsToolBar.add(openAttendanceButton);

        closeAttendanceButton.setAction(checkOutAction);
        closeAttendanceButton.setFocusable(false);
        closeAttendanceButton.setHorizontalTextPosition(SwingConstants.CENTER);
        closeAttendanceButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        closeAttendanceButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        actionsToolBar.add(closeAttendanceButton);
        actionsToolBar.add(attendancesClientsSeparator);

        registerClientButton.setAction(registerClientAction);
        registerClientButton.setFocusable(false);
        registerClientButton.setHorizontalTextPosition(SwingConstants.CENTER);
        registerClientButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        registerClientButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        actionsToolBar.add(registerClientButton);

        editClientButton.setAction(editClientAction);
        editClientButton.setFocusable(false);
        editClientButton.setHorizontalTextPosition(SwingConstants.CENTER);
        editClientButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        editClientButton.setPreferredSize(new java.awt.Dimension(100, 94));
        editClientButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        actionsToolBar.add(editClientButton);
        actionsToolBar.add(clientsFinancesSeparator);

        editOrderButton.setAction(editOrderAction);
        editOrderButton.setFocusable(false);
        editOrderButton.setHorizontalTextPosition(SwingConstants.CENTER);
        editOrderButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        editOrderButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        actionsToolBar.add(editOrderButton);

        return actionsToolBar;
    }

    private void buildMenu() {

        JMenuBar menuBar = new JMenuBar();
        JMenu sessionMenu = new JMenu();
        JMenuItem toggleSessionMenuItem = new JMenuItem();
        JMenuItem toogleRaisedPLSessionMenuItem = new JMenuItem();
        JMenu eventMenu = new JMenu();
        JMenuItem eventCheckInMenuItem = new JMenuItem();
        JMenuItem eventCheckOutMenuItem = new JMenuItem();
        JPopupMenu.Separator eventLeavingPurchaseSeparator = new JPopupMenu.Separator();
        JMenuItem eventPurchaseMenuItem = new JMenuItem();
        JMenu clientsMenu = new JMenu();
        JMenuItem registerClientMenuItem = new JMenuItem();
        JMenuItem findClientMenuItem = new JMenuItem();
        JMenuItem freezeClientMenuItem = new JMenuItem();
        JMenu manageMenu = new JMenu();
        JMenuItem manageItemsMenuItem = new JMenuItem();
        JMenuItem manageSubscriptionsMenuItem = new JMenuItem();
        JMenuItem manageKeysMenuItem = new JMenuItem();
        JMenuItem manageReportsMenuItem = new JMenuItem();
        JPopupMenu.Separator manageSubscriptionsReportsSeparator = new JPopupMenu.Separator();
        JMenuItem manageCashMenuItem = new JMenuItem();
        JMenuItem manageFreezesMenuItem = new JMenuItem();
        JMenu windowMenu = new JMenu();
        JMenuItem attendancesWindowMenuItem = new JMenuItem();
        JMenuItem ordersWindowMenuItem = new JMenuItem();
        JMenuItem itemsWindowMenuItem = new JMenuItem();
        JMenu helpMenu = new JMenu();
        JMenuItem helpAboutMenuItem = new JMenuItem();

        sessionMenu.setText(strings.getString("Menu.Session")); // NOI18N

        toggleSessionMenuItem.setAction(toggleSessionAction);
        toggleSessionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
        sessionMenu.add(toggleSessionMenuItem);

        toogleRaisedPLSessionMenuItem.setAction(toggleRaisedAdministratorAction);
        toogleRaisedPLSessionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        sessionMenu.add(toogleRaisedPLSessionMenuItem);

        menuBar.add(sessionMenu);

        eventMenu.setText(strings.getString("Menu.Events")); // NOI18N

        eventCheckInMenuItem.setAction(checkInAction);
        eventCheckInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK));
        eventMenu.add(eventCheckInMenuItem);

        eventCheckOutMenuItem.setAction(checkOutAction);
        eventCheckOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
        eventMenu.add(eventCheckOutMenuItem);
        eventMenu.add(eventLeavingPurchaseSeparator);

        eventPurchaseMenuItem.setAction(editOrderAction);
        eventPurchaseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));
        eventMenu.add(eventPurchaseMenuItem);

        menuBar.add(eventMenu);

        clientsMenu.setText(strings.getString("Menu.Clients")); // NOI18N

        registerClientMenuItem.setAction(registerClientAction);
        registerClientMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
        clientsMenu.add(registerClientMenuItem);

        findClientMenuItem.setAction(editClientAction);
        findClientMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK));
        clientsMenu.add(findClientMenuItem);

        freezeClientMenuItem.setAction(freezeClientAction);
        freezeClientMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
        clientsMenu.add(freezeClientMenuItem);

        menuBar.add(clientsMenu);

        manageMenu.setText(strings.getString("Menu.Manage")); // NOI18N

        manageItemsMenuItem.setAction(manageItemsAction);
        manageMenu.add(manageItemsMenuItem);

        manageSubscriptionsMenuItem.setAction(manageSubscriptionsAction);
        manageMenu.add(manageSubscriptionsMenuItem);

        manageKeysMenuItem.setAction(manageKeysAction);
        manageMenu.add(manageKeysMenuItem);

        manageMenu.add(manageSubscriptionsReportsSeparator);

        manageReportsMenuItem.setAction(manageReportsAction);
        manageMenu.add(manageReportsMenuItem);

        manageCashMenuItem.setAction(manageCashAction);
        manageMenu.add(manageCashMenuItem);

        manageFreezesMenuItem.setAction(manageFreezesAction);
        manageMenu.add(manageFreezesMenuItem);

        menuBar.add(manageMenu);

        windowMenu.setText(strings.getString("Menu.Window")); // NOI18N

        attendancesWindowMenuItem.setAction(openAttendancesWindowAction);
        windowMenu.add(attendancesWindowMenuItem);

        ordersWindowMenuItem.setAction(openOrdersWindowAction);
        windowMenu.add(ordersWindowMenuItem);

        itemsWindowMenuItem.setAction(openItemsWindowAction);
        windowMenu.add(itemsWindowMenuItem);

        menuBar.add(windowMenu);

        helpMenu.setText(strings.getString("Menu.Help")); // NOI18N

        helpAboutMenuItem.setAction(aboutAction);
        helpMenu.add(helpAboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(55, 85, 111));

        bannerLabel = new JLabel();
        bannerLabel.setIcon(new ImageIcon(getClass().getResource("/org/key2gym/client/resources/banner.png"))); // NOI18N
        panel.add(bannerLabel);

        return panel;
    }

    public void openAttendancesTabForDate(DateMidnight date) throws SecurityViolationException {
        if (attendancesPanels.containsKey(date)) {
            workspacesTabbedPane.setSelectedComponent(attendancesPanels.get(date));
            return;
        }

        final AttendancesPanel attendancesPanel = new AttendancesPanel();
        attendancesPanel.setDate(date);

        workspacesTabbedPane.addTab(null, attendancesPanel);
        attendancesPanels.put(date, attendancesPanel);

        CloseableTabPanel tabComponent = new CloseableTabPanel(MessageFormat.format(strings.getString("Text.AttendancesFor.withDate"), new Object[]{date.toString("dd-MM-yyyy")}));
        tabComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attendancesPanels.remove(attendancesPanel.getDate());
                workspacesTabbedPane.remove(attendancesPanel);
            }
        });
        workspacesTabbedPane.setTabComponentAt(workspacesTabbedPane.indexOfComponent(attendancesPanel), tabComponent);
        workspacesTabbedPane.setSelectedComponent(attendancesPanel);
    }

    public void openOrdersTabForDate(DateMidnight date) throws SecurityViolationException {
        if (ordersPanels.containsKey(date)) {
            workspacesTabbedPane.setSelectedComponent(ordersPanels.get(date));
            return;
        }

        final OrdersPanel ordersPanel = new OrdersPanel();
        ordersPanel.setDate(date);

        workspacesTabbedPane.addTab(null, ordersPanel);
        ordersPanels.put(date, ordersPanel);

        CloseableTabPanel tabComponent = new CloseableTabPanel(MessageFormat.format(strings.getString("Text.OrdersFor.withDate"), new Object[]{date.toString("dd-MM-yyyy")}));
        tabComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ordersPanels.remove(ordersPanel.getDate());
                workspacesTabbedPane.remove(ordersPanel);
            }
        });
        workspacesTabbedPane.setTabComponentAt(workspacesTabbedPane.indexOfComponent(ordersPanel), tabComponent);
        workspacesTabbedPane.setSelectedComponent(ordersPanel);
    }

    public void openItemsTab() throws SecurityViolationException {
        if (itemsPanel != null) {
            workspacesTabbedPane.setSelectedComponent(itemsPanel);
            return;
        }

        itemsPanel = new ItemsPanel();
        itemsPanel.setItems(ItemsPanel.ItemsGroup.PURE);

        workspacesTabbedPane.addTab(strings.getString("Text.Items"), itemsPanel);

        CloseableTabPanel tabComponent = new CloseableTabPanel(strings.getString("Text.Items"));
        tabComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workspacesTabbedPane.remove(itemsPanel);
                itemsPanel = null;
            }
        });
        workspacesTabbedPane.setTabComponentAt(workspacesTabbedPane.indexOfComponent(itemsPanel), tabComponent);
        workspacesTabbedPane.setSelectedComponent(itemsPanel);
    }

    public void createTab(final Component component, String title) {
        workspacesTabbedPane.addTab(title, component);

        CloseableTabPanel tabComponent = new CloseableTabPanel(title);
        tabComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workspacesTabbedPane.remove(component);
            }
        });
        
        int index = workspacesTabbedPane.indexOfComponent(component);
        workspacesTabbedPane.setTabComponentAt(index, tabComponent);
        workspacesTabbedPane.setSelectedIndex(index);
    }

    public AttendanceDTO getSelectedAttendance() {

        Component component = workspacesTabbedPane.getSelectedComponent();

        if (component instanceof AttendancesPanel) {
            return ((AttendancesPanel) component).getSelectedAttendance();
        }

        return null;
    }

    public OrderDTO getSelectedOrder() {
        Component component = workspacesTabbedPane.getSelectedComponent();

        if (component instanceof OrdersPanel) {
            return ((OrdersPanel) component).getSelectedOrder();
        }

        return null;
    }

    private class ContextManagerObserver implements Observer {

        @Override
        public void update(Observable contextManager, Object obj) {
            if (ContextManager.getInstance().isContextAvailable()) {
                try {
                    openAttendancesTabForDate(new DateMidnight());
                    openOrdersTabForDate(new DateMidnight());
                    openItemsTab();
                } catch (SecurityViolationException ex) {
                    Logger.getLogger(this.getClass()).error("Unexpected SecurityViolationException", ex);
                }

                workspacesTabbedPane.setSelectedComponent(attendancesPanels.get(new DateMidnight()));

                AdministratorDTO administrator = ContextManager.lookup(AdministratorsServiceRemote.class)
		    .getCurrent();
                setTitle(MessageFormat.format(strings.getString("Title.Key2Gym.withAdministrator"), new Object[]{administrator.getFullName()}));

            } else {
                workspacesTabbedPane.removeAll();
                attendancesPanels.clear();
                ordersPanels.clear();
                itemsPanel = null;

                setTitle(strings.getString("Title.Key2Gym"));
            }
        }
    }

    /**
     * Process the window closing event. <p> The method must be called once on
     * the MainFrame's shutdown. The application will not shutdown properly, if
     * this method is not called.
     *
     * @param e the window event
     */
    private void onWindowClosing(WindowEvent e) {
        /*
         * Closes the session, if one is open.
         */
        if (ContextManager.getInstance().isContextAvailable()) {
            ContextManager.getInstance().logout();
        }

        /*
         * The main thread is waiting on us to shutdown in order to perform
         * shutdown routine.
         */
        synchronized (MainFrame.getInstance()) {
            setVisible(false);
            MainFrame.getInstance().notify();
        }
    }
    /*
     * Presentation
     */
    private Map<DateMidnight, AttendancesPanel> attendancesPanels;
    private Map<DateMidnight, OrdersPanel> ordersPanels;
    private ItemsPanel itemsPanel;
    private ResourceBundle strings;
    /*
     * Components
     */
    private AboutAction aboutAction;
    private JLabel bannerLabel;
    private CheckOutAction checkOutAction;
    private EditClientAction editClientAction;
    private EditOrderAction editOrderAction;
    private FreezeClientAction freezeClientAction;
    private ManageCashAction manageCashAction;
    private ManageFreezesAction manageFreezesAction;
    private ManageItemsAction manageItemsAction;
    private ManageSubscriptionsAction manageSubscriptionsAction;
    private ManageKeysAction manageKeysAction;
    private ManageReportsAction manageReportsAction;
    private CheckInAction checkInAction;
    private OpenAttendancesWindowAction openAttendancesWindowAction;
    private OpenItemsWindowAction openItemsWindowAction;
    private OpenOrdersWindowAction openOrdersWindowAction;
    private RegisterClientAction registerClientAction;
    private ToggleShadowSessionAction toggleRaisedAdministratorAction;
    private ToggleSessionAction toggleSessionAction;
    private JTabbedPane workspacesTabbedPane;
    /*
     * Singleton instance
     */
    private static MainFrame instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class
     */
    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }
}
