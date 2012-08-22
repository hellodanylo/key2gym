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

package census.presentation;

import census.business.AdministratorsService;
import census.business.SessionsService;
import census.business.StorageService;
import census.business.api.SecurityException;
import census.business.api.SessionListener;
import census.business.dto.AdministratorDTO;
import census.business.dto.AttendanceDTO;
import census.business.dto.OrderDTO;
import census.presentation.panels.AttendancesPanel;
import census.presentation.panels.CloseableTabPanel;
import census.presentation.panels.ItemsPanel;
import census.presentation.panels.OrdersPanel;
import java.awt.Component;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;


/**
 *
 * @author Danylo Vashchilenko
 */
public class MainFrame extends JFrame {
    
    /**
     * Creates new form MainFrame
     */
    protected MainFrame() {
        sessionsService = SessionsService.getInstance();
        sessionsService.addListener(new CustomListener());
        attendancesPanels = new HashMap<>();
        ordersPanels = new HashMap<>();
        bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

        initComponents();

        /*
         * The Starter is waiting on the MainFrame to perform shutting down.
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (MainFrame.getInstance()) {
                    setVisible(false);
                    MainFrame.getInstance().notify();
                }
            }
        });
        
        setLocationRelativeTo(null);
    }

    public Component getComponent() {
        return this.getRootPane();
    }

    public void openAttendancesTabForDate(DateMidnight date) throws SecurityException {
        if (attendancesPanels.containsKey(date)) {
            workspacesTabbedPane.setSelectedComponent(attendancesPanels.get(date));
            return;
        }

        final AttendancesPanel attendancesPanel = new AttendancesPanel();
        attendancesPanel.setDate(date);

        workspacesTabbedPane.addTab(null, attendancesPanel);
        attendancesPanels.put(date, attendancesPanel);

        CloseableTabPanel tabComponent = new CloseableTabPanel();
        tabComponent.setText(MessageFormat.format(bundle.getString("Text.AttendancesFor.withDate"), new Object[] {date.toString("dd-MM-yyyy")}));
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
    public void openOrdersTabForDate(DateMidnight date) throws SecurityException {
        if (ordersPanels.containsKey(date)) {
            workspacesTabbedPane.setSelectedComponent(ordersPanels.get(date));
            return;
        }

        final OrdersPanel ordersPanel = new OrdersPanel();
        ordersPanel.setDate(date);

        workspacesTabbedPane.addTab(null, ordersPanel);
        ordersPanels.put(date, ordersPanel);

        CloseableTabPanel tabComponent = new CloseableTabPanel();
        tabComponent.setText(MessageFormat.format(bundle.getString("Text.OrdersFor.withDate"), new Object[] {date.toString("dd-MM-yyyy")}));
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

    public void openItemsTab() {        
        if (itemsPanel != null) {
            workspacesTabbedPane.setSelectedComponent(itemsPanel);
            return;
        }

        itemsPanel = new ItemsPanel();
        itemsPanel.setItems(ItemsPanel.ItemsGroup.PURE);

        workspacesTabbedPane.addTab(bundle.getString("Text.Items"), itemsPanel);

        CloseableTabPanel tabComponent = new CloseableTabPanel();
        tabComponent.setText(bundle.getString("Text.Items"));
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

    public AttendanceDTO getSelectedAttendance() {

        Component component = workspacesTabbedPane.getSelectedComponent();

        if (component instanceof AttendancesPanel) {
            return ((AttendancesPanel) component).getSelectedAttendance();
        }

        return null;
    }
    
    public OrderDTO getSelectedOrder() {
        Component component = workspacesTabbedPane.getSelectedComponent();
        
        if(component instanceof OrdersPanel) {
            return ((OrdersPanel)component).getSelectedOrder();
        }
        
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toggleRaisedAdministratorAction = new census.presentation.actions.ToggleRaisedAdministratorAction();
        freezeClientAction = new census.presentation.actions.FreezeClientAction();
        manageCashAction = new census.presentation.actions.ManageCashAction();
        manageFreezesAction = new census.presentation.actions.ManageFreezesAction();
        editOrderAction = new census.presentation.actions.EditOrderAction();
        openAttendancesWindowAction = new census.presentation.actions.OpenAttendancesWindowAction();
        openOrdersWindowAction = new census.presentation.actions.OpenOrdersWindowAction();
        editClientAction = new census.presentation.actions.EditClientAction();
        closeAttendanceAction = new census.presentation.actions.CheckOutAction();
        openAttendanceAction = new census.presentation.actions.CheckInAction();
        registerClientAction = new census.presentation.actions.RegisterClientAction();
        manageItemsAction = new census.presentation.actions.ManageItemsAction();
        manageSubscriptionsAction = new census.presentation.actions.ManageSubscriptionsAction();
        toggleSessionAction = new census.presentation.actions.ToggleSessionAction();
        openItemsWindowAction = new census.presentation.actions.OpenItemsWindowAction();
        aboutAction = new census.presentation.actions.AboutAction();
        actionsToolBar = new javax.swing.JToolBar();
        openAttendanceButton = new javax.swing.JButton();
        closeAttendanceButton = new javax.swing.JButton();
        attendancesClientsSeparator = new javax.swing.JToolBar.Separator();
        registerClientButton = new javax.swing.JButton();
        editClientButton = new javax.swing.JButton();
        clientsFinancesSeparator = new javax.swing.JToolBar.Separator();
        editFinancialActivityButton = new javax.swing.JButton();
        workspacesTabbedPane = new javax.swing.JTabbedPane();
        bannerLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        sessionMenu = new javax.swing.JMenu();
        toggleSessionMenuItem = new javax.swing.JMenuItem();
        toogleRaisedPLSessionMenuItem = new javax.swing.JMenuItem();
        eventMenu = new javax.swing.JMenu();
        eventEntryMenuItem = new javax.swing.JMenuItem();
        eventLeavingMenuItem = new javax.swing.JMenuItem();
        eventLeavingPurchaseSeparator = new javax.swing.JPopupMenu.Separator();
        eventPurchaseMenuItem = new javax.swing.JMenuItem();
        clientsMenu = new javax.swing.JMenu();
        registerClientMenuItem = new javax.swing.JMenuItem();
        findClientMenuItem = new javax.swing.JMenuItem();
        freezeClientMenuItem = new javax.swing.JMenuItem();
        manageMenu = new javax.swing.JMenu();
        manageItemsMenuItem = new javax.swing.JMenuItem();
        manageSubscriptionsMenuItem = new javax.swing.JMenuItem();
        manageSubscriptionsCashSeparator = new javax.swing.JPopupMenu.Separator();
        manageCash = new javax.swing.JMenuItem();
        manageCashFreezesSeparator = new javax.swing.JPopupMenu.Separator();
        manageFreezesMenuItem = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        attendancesWindowMenuItem = new javax.swing.JMenuItem();
        ordersWindowMenuItem = new javax.swing.JMenuItem();
        itemsWindowMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("Title.Census")); // NOI18N

        actionsToolBar.setFloatable(false);
        actionsToolBar.setRollover(true);

        openAttendanceButton.setAction(openAttendanceAction);
        openAttendanceButton.setFocusable(false);
        openAttendanceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openAttendanceButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        openAttendanceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        actionsToolBar.add(openAttendanceButton);

        closeAttendanceButton.setAction(closeAttendanceAction);
        closeAttendanceButton.setFocusable(false);
        closeAttendanceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeAttendanceButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        closeAttendanceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        actionsToolBar.add(closeAttendanceButton);
        actionsToolBar.add(attendancesClientsSeparator);

        registerClientButton.setAction(registerClientAction);
        registerClientButton.setFocusable(false);
        registerClientButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        registerClientButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        registerClientButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        actionsToolBar.add(registerClientButton);

        editClientButton.setAction(editClientAction);
        editClientButton.setFocusable(false);
        editClientButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editClientButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        editClientButton.setPreferredSize(new java.awt.Dimension(100, 94));
        editClientButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        actionsToolBar.add(editClientButton);
        actionsToolBar.add(clientsFinancesSeparator);

        editFinancialActivityButton.setAction(editOrderAction);
        editFinancialActivityButton.setFocusable(false);
        editFinancialActivityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editFinancialActivityButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        editFinancialActivityButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        actionsToolBar.add(editFinancialActivityButton);

        bannerLabel.setBackground(new java.awt.Color(55, 85, 111));
        bannerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bannerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/census/presentation/resources/banner.png"))); // NOI18N
        bannerLabel.setOpaque(true);

        sessionMenu.setText(bundle.getString("Menu.Session")); // NOI18N

        toggleSessionMenuItem.setAction(toggleSessionAction);
        toggleSessionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        sessionMenu.add(toggleSessionMenuItem);

        toogleRaisedPLSessionMenuItem.setAction(toggleRaisedAdministratorAction);
        toogleRaisedPLSessionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        sessionMenu.add(toogleRaisedPLSessionMenuItem);

        menuBar.add(sessionMenu);

        eventMenu.setText(bundle.getString("Menu.Events")); // NOI18N

        eventEntryMenuItem.setAction(openAttendanceAction);
        eventEntryMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK));
        eventMenu.add(eventEntryMenuItem);

        eventLeavingMenuItem.setAction(closeAttendanceAction);
        eventLeavingMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        eventMenu.add(eventLeavingMenuItem);
        eventMenu.add(eventLeavingPurchaseSeparator);

        eventPurchaseMenuItem.setAction(editOrderAction);
        eventPurchaseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK));
        eventMenu.add(eventPurchaseMenuItem);

        menuBar.add(eventMenu);

        clientsMenu.setText(bundle.getString("Menu.Clients")); // NOI18N

        registerClientMenuItem.setAction(registerClientAction);
        registerClientMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        clientsMenu.add(registerClientMenuItem);

        findClientMenuItem.setAction(editClientAction);
        findClientMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        clientsMenu.add(findClientMenuItem);

        freezeClientMenuItem.setAction(freezeClientAction);
        freezeClientMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        clientsMenu.add(freezeClientMenuItem);

        menuBar.add(clientsMenu);

        manageMenu.setText(bundle.getString("Menu.Manage")); // NOI18N

        manageItemsMenuItem.setAction(manageItemsAction);
        manageMenu.add(manageItemsMenuItem);

        manageSubscriptionsMenuItem.setAction(manageSubscriptionsAction);
        manageMenu.add(manageSubscriptionsMenuItem);
        manageMenu.add(manageSubscriptionsCashSeparator);

        manageCash.setAction(manageCashAction);
        manageMenu.add(manageCash);
        manageMenu.add(manageCashFreezesSeparator);

        manageFreezesMenuItem.setAction(manageFreezesAction);
        manageMenu.add(manageFreezesMenuItem);

        menuBar.add(manageMenu);

        windowMenu.setText(bundle.getString("Menu.Window")); // NOI18N

        attendancesWindowMenuItem.setAction(openAttendancesWindowAction);
        windowMenu.add(attendancesWindowMenuItem);

        ordersWindowMenuItem.setAction(openOrdersWindowAction);
        windowMenu.add(ordersWindowMenuItem);

        itemsWindowMenuItem.setAction(openItemsWindowAction);
        windowMenu.add(itemsWindowMenuItem);

        menuBar.add(windowMenu);

        jMenu1.setText(bundle.getString("Menu.Help")); // NOI18N

        jMenuItem1.setAction(aboutAction);
        jMenu1.add(jMenuItem1);

        menuBar.add(jMenu1);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(actionsToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
            .addComponent(workspacesTabbedPane)
            .addComponent(bannerLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(actionsToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workspacesTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bannerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private class CustomListener implements SessionListener {

        @Override
        public void sessionOpened() {
            try {
                openAttendancesTabForDate(new DateMidnight());
                openOrdersTabForDate(new DateMidnight());
            } catch (SecurityException ex) {
                Logger.getLogger(this.getClass().getName()).error("Unexpected SecurityException", ex);
            }

            openItemsTab();

            workspacesTabbedPane.setSelectedComponent(attendancesPanels.get(new DateMidnight()));

            AdministratorDTO administrator = AdministratorsService.getInstance().getById(sessionsService.getTopmostAdministratorId());
            setTitle(MessageFormat.format(bundle.getString("Title.Census.withAdministrator"), new Object[] {administrator.getFullName()}));
        }

        @Override
        public void sessionClosed() {
            workspacesTabbedPane.removeAll();
            attendancesPanels.clear();
            ordersPanels.clear();
            itemsPanel = null;

            setTitle(bundle.getString("Title.Census"));
        }

        @Override
        public void sessionUpdated() {
            AdministratorDTO administrator = AdministratorsService.getInstance().getById(sessionsService.getTopmostAdministratorId());
            setTitle(MessageFormat.format(bundle.getString("Title.Census.withAdministrator"), new Object[] {administrator.getFullName()}));
        }
    }
    
    @Override
    public void dispose() {      
        if(sessionsService.hasOpenSession()) {
            StorageService.getInstance().beginTransaction();
            sessionsService.closeSession();
            StorageService.getInstance().commitTransaction();
        }
        super.dispose();
    }

    /*
     * Presentation
     */
    private Map<DateMidnight, AttendancesPanel> attendancesPanels;
    private Map<DateMidnight, OrdersPanel> ordersPanels;
    private ItemsPanel itemsPanel;
    private ResourceBundle bundle;
    /*
     * Business
     */
    private SessionsService sessionsService;
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private census.presentation.actions.AboutAction aboutAction;
    private javax.swing.JToolBar actionsToolBar;
    private javax.swing.JToolBar.Separator attendancesClientsSeparator;
    private javax.swing.JMenuItem attendancesWindowMenuItem;
    private javax.swing.JLabel bannerLabel;
    private javax.swing.JToolBar.Separator clientsFinancesSeparator;
    private javax.swing.JMenu clientsMenu;
    private census.presentation.actions.CheckOutAction closeAttendanceAction;
    private javax.swing.JButton closeAttendanceButton;
    private census.presentation.actions.EditClientAction editClientAction;
    private javax.swing.JButton editClientButton;
    private javax.swing.JButton editFinancialActivityButton;
    private census.presentation.actions.EditOrderAction editOrderAction;
    private javax.swing.JMenuItem eventEntryMenuItem;
    private javax.swing.JMenuItem eventLeavingMenuItem;
    private javax.swing.JPopupMenu.Separator eventLeavingPurchaseSeparator;
    private javax.swing.JMenu eventMenu;
    private javax.swing.JMenuItem eventPurchaseMenuItem;
    private javax.swing.JMenuItem findClientMenuItem;
    private census.presentation.actions.FreezeClientAction freezeClientAction;
    private javax.swing.JMenuItem freezeClientMenuItem;
    private javax.swing.JMenuItem itemsWindowMenuItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem manageCash;
    private census.presentation.actions.ManageCashAction manageCashAction;
    private javax.swing.JPopupMenu.Separator manageCashFreezesSeparator;
    private census.presentation.actions.ManageFreezesAction manageFreezesAction;
    private javax.swing.JMenuItem manageFreezesMenuItem;
    private census.presentation.actions.ManageItemsAction manageItemsAction;
    private javax.swing.JMenuItem manageItemsMenuItem;
    private javax.swing.JMenu manageMenu;
    private census.presentation.actions.ManageSubscriptionsAction manageSubscriptionsAction;
    private javax.swing.JPopupMenu.Separator manageSubscriptionsCashSeparator;
    private javax.swing.JMenuItem manageSubscriptionsMenuItem;
    private javax.swing.JMenuBar menuBar;
    private census.presentation.actions.CheckInAction openAttendanceAction;
    private javax.swing.JButton openAttendanceButton;
    private census.presentation.actions.OpenAttendancesWindowAction openAttendancesWindowAction;
    private census.presentation.actions.OpenItemsWindowAction openItemsWindowAction;
    private census.presentation.actions.OpenOrdersWindowAction openOrdersWindowAction;
    private javax.swing.JMenuItem ordersWindowMenuItem;
    private census.presentation.actions.RegisterClientAction registerClientAction;
    private javax.swing.JButton registerClientButton;
    private javax.swing.JMenuItem registerClientMenuItem;
    private javax.swing.JMenu sessionMenu;
    private census.presentation.actions.ToggleRaisedAdministratorAction toggleRaisedAdministratorAction;
    private census.presentation.actions.ToggleSessionAction toggleSessionAction;
    private javax.swing.JMenuItem toggleSessionMenuItem;
    private javax.swing.JMenuItem toogleRaisedPLSessionMenuItem;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JTabbedPane workspacesTabbedPane;
    // End of variables declaration//GEN-END:variables

}
