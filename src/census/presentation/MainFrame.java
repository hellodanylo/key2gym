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
import census.presentation.actions.*;
import census.presentation.panels.AttendancesPanel;
import census.presentation.panels.CloseableTabPanel;
import census.presentation.panels.ItemsPanel;
import census.presentation.panels.OrdersPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
     * Creates new MainFrame
     */
    protected MainFrame() {
        sessionsService = SessionsService.getInstance();
        sessionsService.addListener(new CustomListener());
        
        attendancesPanels = new HashMap<>();
        ordersPanels = new HashMap<>();
        
        bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

        initComponents();

        /*
         * The Starter is waiting on the MainFrame to close.
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
        setTitle(bundle.getString("Title.Census")); // NOI18N
        pack();
        setLocationRelativeTo(null);
    }                
    
    private void initActions() {
        toggleRaisedAdministratorAction = new ToggleRaisedAdministratorAction();
        freezeClientAction = new FreezeClientAction();
        manageCashAction = new ManageCashAction();
        manageFreezesAction = new ManageFreezesAction();
        editOrderAction = new EditOrderAction();
        openAttendancesWindowAction = new OpenAttendancesWindowAction();
        openOrdersWindowAction = new OpenOrdersWindowAction();
        editClientAction = new EditClientAction();
        closeAttendanceAction = new CheckOutAction();
        openAttendanceAction = new CheckInAction();
        registerClientAction = new RegisterClientAction();
        manageItemsAction = new ManageItemsAction();
        manageSubscriptionsAction = new ManageSubscriptionsAction();
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

        openAttendanceButton.setAction(openAttendanceAction);
        openAttendanceButton.setFocusable(false);
        openAttendanceButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openAttendanceButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        openAttendanceButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        actionsToolBar.add(openAttendanceButton);

        closeAttendanceButton.setAction(closeAttendanceAction);
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
        JMenuItem eventEntryMenuItem = new JMenuItem();
        JMenuItem eventLeavingMenuItem = new JMenuItem();
        JPopupMenu.Separator eventLeavingPurchaseSeparator = new JPopupMenu.Separator();
        JMenuItem eventPurchaseMenuItem = new JMenuItem();
        JMenu clientsMenu = new JMenu();
        JMenuItem registerClientMenuItem = new JMenuItem();
        JMenuItem findClientMenuItem = new JMenuItem();
        JMenuItem freezeClientMenuItem = new JMenuItem();
        JMenu manageMenu = new JMenu();
        JMenuItem manageItemsMenuItem = new JMenuItem();
        JMenuItem manageSubscriptionsMenuItem = new JMenuItem();
        JPopupMenu.Separator manageSubscriptionsCashSeparator = new JPopupMenu.Separator();
        JMenuItem manageCash = new JMenuItem();
        JPopupMenu.Separator manageCashFreezesSeparator = new JPopupMenu.Separator();
        JMenuItem manageFreezesMenuItem = new JMenuItem();
        JMenu windowMenu = new JMenu();
        JMenuItem attendancesWindowMenuItem = new JMenuItem();
        JMenuItem ordersWindowMenuItem = new JMenuItem();
        JMenuItem itemsWindowMenuItem = new JMenuItem();
        JMenu helpMenu = new JMenu();
        JMenuItem helpAboutMenuItem = new JMenuItem();
        
        sessionMenu.setText(bundle.getString("Menu.Session")); // NOI18N

        toggleSessionMenuItem.setAction(toggleSessionAction);
        toggleSessionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
        sessionMenu.add(toggleSessionMenuItem);

        toogleRaisedPLSessionMenuItem.setAction(toggleRaisedAdministratorAction);
        toogleRaisedPLSessionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        sessionMenu.add(toogleRaisedPLSessionMenuItem);

        menuBar.add(sessionMenu);

        eventMenu.setText(bundle.getString("Menu.Events")); // NOI18N

        eventEntryMenuItem.setAction(openAttendanceAction);
        eventEntryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK));
        eventMenu.add(eventEntryMenuItem);

        eventLeavingMenuItem.setAction(closeAttendanceAction);
        eventLeavingMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
        eventMenu.add(eventLeavingMenuItem);
        eventMenu.add(eventLeavingPurchaseSeparator);

        eventPurchaseMenuItem.setAction(editOrderAction);
        eventPurchaseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));
        eventMenu.add(eventPurchaseMenuItem);

        menuBar.add(eventMenu);

        clientsMenu.setText(bundle.getString("Menu.Clients")); // NOI18N

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

        helpMenu.setText(bundle.getString("Menu.Help")); // NOI18N

        helpAboutMenuItem.setAction(aboutAction);
        helpMenu.add(helpAboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new java.awt.Color(55, 85, 111));   
        
        bannerLabel = new JLabel();
        bannerLabel.setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/banner.png"))); // NOI18N
        panel.add(bannerLabel);
        
        return panel;
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
     * Business
     */
    private SessionsService sessionsService;
    
        /*
     * Presentation
     */
    private Map<DateMidnight, AttendancesPanel> attendancesPanels;
    private Map<DateMidnight, OrdersPanel> ordersPanels;
    private ItemsPanel itemsPanel;
    private ResourceBundle bundle;
    
    /*
     * Components
     */              
    private AboutAction aboutAction;
    private JLabel bannerLabel;
    private CheckOutAction closeAttendanceAction;
    private EditClientAction editClientAction;
    private EditOrderAction editOrderAction;
    private FreezeClientAction freezeClientAction;
    private ManageCashAction manageCashAction;
    private ManageFreezesAction manageFreezesAction;
    private ManageItemsAction manageItemsAction;
    private ManageSubscriptionsAction manageSubscriptionsAction;
    private CheckInAction openAttendanceAction;
    private OpenAttendancesWindowAction openAttendancesWindowAction;
    private OpenItemsWindowAction openItemsWindowAction;
    private OpenOrdersWindowAction openOrdersWindowAction;
    private RegisterClientAction registerClientAction;
    private ToggleRaisedAdministratorAction toggleRaisedAdministratorAction;
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
