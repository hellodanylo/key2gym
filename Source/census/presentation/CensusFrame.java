/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation;

import census.business.AdministratorsService;
import census.business.SessionsService;
import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.SessionListener;
import census.business.api.ValidationException;
import census.business.dto.AdministratorDTO;
import census.business.dto.AttendanceDTO;
import census.business.dto.FinancialActivityDTO;
import census.presentation.blocks.AttendancesPanel;
import census.presentation.blocks.CloseableTabComponent;
import census.presentation.blocks.FinancialActivitiesPanel;
import census.presentation.blocks.ItemsPanel;
import census.presentation.util.CensusExceptionListener;
import census.presentation.util.NotificationException;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.FocusManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.joda.time.DateMidnight;


/**
 *
 * @author Danylo Vashchilenko
 */
public class CensusFrame extends JFrame {
    /**
     * Creates new form CensusFrame
     */
    protected CensusFrame() {
        sessionsService = SessionsService.getInstance();
        sessionsService.addListener(new CustomListener());
        attendancesPanels = new HashMap<>();
        financialActivitiesPanels = new HashMap<>();
        bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

        initComponents();

        getGlobalCensusExceptionListenersStack().push(new CensusFrameExceptionListener());

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

        CloseableTabComponent tabComponent = new CloseableTabComponent();
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
    public void openFinancialActivitiesTabForDate(DateMidnight date) throws SecurityException {
        if (financialActivitiesPanels.containsKey(date)) {
            workspacesTabbedPane.setSelectedComponent(financialActivitiesPanels.get(date));
            return;
        }

        final FinancialActivitiesPanel financialActivitiesPanel = new FinancialActivitiesPanel();
        financialActivitiesPanel.setDate(date);

        workspacesTabbedPane.addTab(null, financialActivitiesPanel);
        financialActivitiesPanels.put(date, financialActivitiesPanel);

        CloseableTabComponent tabComponent = new CloseableTabComponent();
        tabComponent.setText(MessageFormat.format(bundle.getString("Text.FinancialActivitiesFor.withDate"), new Object[] {date.toString("dd-MM-yyyy")}));
        tabComponent.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                financialActivitiesPanels.remove(financialActivitiesPanel.getDate());
                workspacesTabbedPane.remove(financialActivitiesPanel);
            }
        });
        workspacesTabbedPane.setTabComponentAt(workspacesTabbedPane.indexOfComponent(financialActivitiesPanel), tabComponent);
        workspacesTabbedPane.setSelectedComponent(financialActivitiesPanel);
    }

    public void openItemsTab() {
        if (itemsPanel != null) {
            workspacesTabbedPane.setSelectedComponent(itemsPanel);
            return;
        }

        itemsPanel = new ItemsPanel();
        itemsPanel.setItems(ItemsPanel.Items.PURE);
        workspacesTabbedPane.addTab(bundle.getString("Text.Items"), itemsPanel);
    }

    public AttendanceDTO getSelectedAttendance() {

        Component component = workspacesTabbedPane.getSelectedComponent();

        if (component instanceof AttendancesPanel) {
            return ((AttendancesPanel) component).getSelectedAttendance();
        }

        return null;
    }
    
    public FinancialActivityDTO getSelectedFinancialActivity() {
        Component component = workspacesTabbedPane.getSelectedComponent();
        
        if(component instanceof FinancialActivitiesPanel) {
            return ((FinancialActivitiesPanel)component).getSelectedFinancialActivity();
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
        editFinancialActivityAction = new census.presentation.actions.EditFinancialActivityAction();
        openAttendancesWindowAction = new census.presentation.actions.OpenAttendancesWindowAction();
        openFinancialActivitiesWindowAction = new census.presentation.actions.OpenFinancialActivitiesWindowAction();
        editClientAction = new census.presentation.actions.EditClientAction();
        closeAttendanceAction = new census.presentation.actions.CloseAttendanceAction();
        openAttendanceAction = new census.presentation.actions.OpenAttendanceAction();
        registerClientAction = new census.presentation.actions.RegisterClientAction();
        manageItemsAction = new census.presentation.actions.ManageItemsAction();
        manageSubscriptionsAction = new census.presentation.actions.ManageSubscriptionsAction();
        toggleSessionAction = new census.presentation.actions.ToggleSessionAction();
        actionsToolBar = new javax.swing.JToolBar();
        openAttendanceButton = new javax.swing.JButton();
        closeAttendanceButton = new javax.swing.JButton();
        attendanceClientSeparator = new javax.swing.JToolBar.Separator();
        registerClientButton = new javax.swing.JButton();
        editClientButton = new javax.swing.JButton();
        clientFinancialActivitySeparator = new javax.swing.JToolBar.Separator();
        editFinancialActivityButton = new javax.swing.JButton();
        workspacesTabbedPane = new javax.swing.JTabbedPane();
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
        clientsRegisterMenuItem = new javax.swing.JMenuItem();
        clientsEditMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        manageMenu = new javax.swing.JMenu();
        manageItemsMenuItem = new javax.swing.JMenuItem();
        manageSubscriptionsMenuItem = new javax.swing.JMenuItem();
        manageSubscriptionsCashSeparator = new javax.swing.JPopupMenu.Separator();
        manageCash = new javax.swing.JMenuItem();
        manageCashFreezesSeparator = new javax.swing.JPopupMenu.Separator();
        manageFreezesMenuItem = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        attendancesWindowMenuItem = new javax.swing.JMenuItem();
        financialActivitiesWindowMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
        actionsToolBar.add(attendanceClientSeparator);

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
        actionsToolBar.add(clientFinancialActivitySeparator);

        editFinancialActivityButton.setAction(editFinancialActivityAction);
        editFinancialActivityButton.setFocusable(false);
        editFinancialActivityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editFinancialActivityButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        actionsToolBar.add(editFinancialActivityButton);

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

        eventPurchaseMenuItem.setAction(editFinancialActivityAction);
        eventPurchaseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK));
        eventMenu.add(eventPurchaseMenuItem);

        menuBar.add(eventMenu);

        clientsMenu.setText(bundle.getString("Menu.Clients")); // NOI18N

        clientsRegisterMenuItem.setAction(registerClientAction);
        clientsRegisterMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        clientsMenu.add(clientsRegisterMenuItem);

        clientsEditMenuItem.setAction(editClientAction);
        clientsEditMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        clientsMenu.add(clientsEditMenuItem);

        jMenuItem1.setAction(freezeClientAction);
        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        clientsMenu.add(jMenuItem1);

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
        attendancesWindowMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.ALT_MASK));
        windowMenu.add(attendancesWindowMenuItem);

        financialActivitiesWindowMenuItem.setAction(openFinancialActivitiesWindowAction);
        financialActivitiesWindowMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.ALT_MASK));
        windowMenu.add(financialActivitiesWindowMenuItem);

        menuBar.add(windowMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(actionsToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 865, Short.MAX_VALUE)
            .addComponent(workspacesTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(actionsToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workspacesTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private class CustomListener implements SessionListener {

        @Override
        public void sessionOpened() {
            try {
                openAttendancesTabForDate(new DateMidnight());
                openFinancialActivitiesTabForDate(new DateMidnight());
            } catch (SecurityException ex) {
                Logger.getLogger(CensusFrame.class.getName()).log(Level.SEVERE, null, ex);
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
            financialActivitiesPanels.clear();
            itemsPanel = null;

            setTitle(bundle.getString("Title.Census"));
        }

        @Override
        public void sessionUpdated() {
            AdministratorDTO administrator = AdministratorsService.getInstance().getById(sessionsService.getTopmostAdministratorId());
            setTitle(MessageFormat.format(bundle.getString("Title.Census.withAdministrator"), new Object[] {administrator.getFullName()}));
        }
    }

    /*
     * Presentation
     */
    private Map<DateMidnight, AttendancesPanel> attendancesPanels;
    private Map<DateMidnight, FinancialActivitiesPanel> financialActivitiesPanels;
    private ItemsPanel itemsPanel;
    private ResourceBundle bundle;
    /*
     * Business
     */
    private SessionsService sessionsService;
    /*
     * Singleton instance
     */
    private static CensusFrame instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class
     */
    public static CensusFrame getInstance() {
        if (instance == null) {
            instance = new CensusFrame();
        }
        return instance;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar actionsToolBar;
    private javax.swing.JToolBar.Separator attendanceClientSeparator;
    private javax.swing.JMenuItem attendancesWindowMenuItem;
    private javax.swing.JToolBar.Separator clientFinancialActivitySeparator;
    private javax.swing.JMenuItem clientsEditMenuItem;
    private javax.swing.JMenu clientsMenu;
    private javax.swing.JMenuItem clientsRegisterMenuItem;
    private census.presentation.actions.CloseAttendanceAction closeAttendanceAction;
    private javax.swing.JButton closeAttendanceButton;
    private census.presentation.actions.EditClientAction editClientAction;
    private javax.swing.JButton editClientButton;
    private census.presentation.actions.EditFinancialActivityAction editFinancialActivityAction;
    private javax.swing.JButton editFinancialActivityButton;
    private javax.swing.JMenuItem eventEntryMenuItem;
    private javax.swing.JMenuItem eventLeavingMenuItem;
    private javax.swing.JPopupMenu.Separator eventLeavingPurchaseSeparator;
    private javax.swing.JMenu eventMenu;
    private javax.swing.JMenuItem eventPurchaseMenuItem;
    private javax.swing.JMenuItem financialActivitiesWindowMenuItem;
    private census.presentation.actions.FreezeClientAction freezeClientAction;
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
    private census.presentation.actions.OpenAttendanceAction openAttendanceAction;
    private javax.swing.JButton openAttendanceButton;
    private census.presentation.actions.OpenAttendancesWindowAction openAttendancesWindowAction;
    private census.presentation.actions.OpenFinancialActivitiesWindowAction openFinancialActivitiesWindowAction;
    private census.presentation.actions.RegisterClientAction registerClientAction;
    private javax.swing.JButton registerClientButton;
    private javax.swing.JMenu sessionMenu;
    private census.presentation.actions.ToggleRaisedAdministratorAction toggleRaisedAdministratorAction;
    private census.presentation.actions.ToggleSessionAction toggleSessionAction;
    private javax.swing.JMenuItem toggleSessionMenuItem;
    private javax.swing.JMenuItem toogleRaisedPLSessionMenuItem;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JTabbedPane workspacesTabbedPane;
    // End of variables declaration//GEN-END:variables

    /*
     * Global BusinessExceptionsListeners stack. The top listener is the
     * listener that is supposed to process all incoming BusinessExceptions.
     */
    private static Stack<CensusExceptionListener> globalListenersStack = new Stack<>();

    public static Stack<CensusExceptionListener> getGlobalCensusExceptionListenersStack() {
        return globalListenersStack;


    }

    public class CensusFrameExceptionListener implements CensusExceptionListener {

        public Component getComponent() {
            return FocusManager.getCurrentManager().getFocusedWindow();
        }

        @Override
        public void processException(Exception ex) {
            Integer messageType;
            if (ex instanceof NotificationException) {
                messageType = JOptionPane.INFORMATION_MESSAGE;
            } else if (ex instanceof ValidationException || ex instanceof SecurityException) {
                messageType = JOptionPane.WARNING_MESSAGE;
            } else if (ex instanceof BusinessException) {
                messageType = JOptionPane.ERROR_MESSAGE;
            } else {
                throw new RuntimeException("Unexpexted exception type.");
            }
            JOptionPane.showMessageDialog(getComponent(), ex.getMessage(), bundle.getString("Title.Message"), messageType);
        }
    }
}
