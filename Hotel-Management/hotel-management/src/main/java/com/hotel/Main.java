package com.hotel;

import com.hotel.controller.BookingController;
import com.hotel.controller.CustomerController;
import com.hotel.controller.EmployeeController;
import com.hotel.controller.InvoiceController;
import com.hotel.controller.PaymentController;
import com.hotel.controller.RoomController;
import com.hotel.controller.RoomTypeController;
import com.hotel.controller.ServiceCatalogController;
import com.hotel.controller.ServiceUsageController;
import com.hotel.security.AuthSession;
import com.hotel.security.EmployeeRoles;
import com.hotel.view.AddServiceView;
import com.hotel.view.BookView;
import com.hotel.view.CheckOutView;
import com.hotel.view.CustomerView;
import com.hotel.view.EmployeeView;
import com.hotel.view.InvoiceReportView;
import com.hotel.view.LoginDialog;
import com.hotel.view.RoomPanel;
import com.hotel.view.RoomManagementView;
import com.hotel.view.ServiceView;
import com.hotel.view.UiMessages;
import com.hotel.view.UiStyles;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContainer = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private RoomPanel homePanel;
    private BookView bookView;
    private RoomManagementView roomManagementView;
    private InvoiceReportView invoiceReportView;
    private CheckOutView checkOutView;
    private AddServiceView addServiceView;
    private JLabel currentUserLabel;
    private EmployeeController employeeController;

    public Main() {
        setTitle("Hotel Management System");
        setSize(1400, 1100);
        setMinimumSize(new Dimension(1180, 760));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiStyles.BACKGROUND);

        employeeController = new EmployeeController();
        employeeController.ensureDefaultManagerExists();
        if (!promptLogin()) {
            dispose();
            System.exit(0);
            return;
        }

        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyles.BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);
        JLabel appTitle = UiStyles.createTitle("Hotel Management");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel subtitle = UiStyles.createHint("Manage rooms, bookings, services and checkout in one place.");
        titlePanel.add(appTitle);
        titlePanel.add(subtitle);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        menuPanel.setOpaque(false);

        String[] menuItems = {"Home", "Book", "Rooms", "Service", "Customer", "Employee", "Invoices"};
        for (String item : menuItems) {
            JButton btn = createNavButton(item);
            btn.setIcon(UiStyles.resolveMenuIcon(item));
            btn.setIconTextGap(8);
            String cardName = item.toUpperCase();
            navButtons.put(cardName, btn);
            btn.addActionListener(e -> showCard(cardName));
            menuPanel.add(btn);
        }

        BookingController bookingController = new BookingController();
        CustomerController customerController = new CustomerController();
        RoomController roomController = new RoomController();
        RoomTypeController roomTypeController = new RoomTypeController();
        ServiceCatalogController serviceCatalogController = new ServiceCatalogController();
        ServiceUsageController serviceUsageController = new ServiceUsageController();
        InvoiceController invoiceController = new InvoiceController();
        PaymentController paymentController = new PaymentController();

        homePanel = new RoomPanel(this, roomController, bookingController);
        bookView = new BookView(this, bookingController, customerController, roomController);
        roomManagementView = new RoomManagementView(roomController, roomTypeController);
        invoiceReportView = new InvoiceReportView(invoiceController, paymentController);
        mainContainer.setBackground(UiStyles.BACKGROUND);
        mainContainer.add(homePanel, "HOME");
        mainContainer.add(bookView, "BOOK");
        mainContainer.add(roomManagementView, "ROOMS");
        mainContainer.add(new ServiceView(serviceCatalogController), "SERVICE");
        mainContainer.add(new CustomerView(customerController), "CUSTOMER");
        mainContainer.add(new EmployeeView(employeeController), "EMPLOYEE");
        mainContainer.add(invoiceReportView, "INVOICES");
        checkOutView = new CheckOutView(invoiceController, paymentController, bookingController, serviceUsageController, this::refreshHomeRooms);
        mainContainer.add(checkOutView, "CHECKOUT");
        addServiceView = new AddServiceView(serviceUsageController, serviceCatalogController, bookingController);
        mainContainer.add(addServiceView, "ADD_SERVICE");

        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        accountPanel.setOpaque(false);
        currentUserLabel = UiStyles.createHint("");
        JButton btnLogout = createNavButton("Logout");
        btnLogout.setIcon(UiStyles.resolveMenuIcon("Logout"));
        btnLogout.setIconTextGap(8);
        btnLogout.addActionListener(e -> handleLogout());
        accountPanel.add(currentUserLabel);
        accountPanel.add(btnLogout);

        JPanel rightHeaderPanel = new JPanel(new BorderLayout(0, 10));
        rightHeaderPanel.setOpaque(false);
        rightHeaderPanel.add(menuPanel, BorderLayout.CENTER);
        rightHeaderPanel.add(accountPanel, BorderLayout.SOUTH);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);

        applyPermissions();
        setLocationRelativeTo(null);
        setVisible(true);
        showDefaultCardForRole();
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setBackground(UiStyles.PANEL_ALT);
        btn.setForeground(UiStyles.TEXT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyles.BORDER, 1, true),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        UiStyles.installButtonDisabledStyle(btn, UiStyles.PANEL_ALT, UiStyles.TEXT);
        return btn;
    }

    public void refreshHomeRooms() {
        if (homePanel != null) {
            homePanel.refreshRooms();
        }
        if (bookView != null) {
            bookView.refreshData();
        }
        if (roomManagementView != null) {
            roomManagementView.refreshData();
        }
        if (invoiceReportView != null) {
            invoiceReportView.refreshData();
        }
    }

    public void showCard(String cardName) {
        if (!isCardAllowed(cardName)) {
            UiMessages.showWarning(this, "You do not have permission to access this section.");
            showDefaultCardForRole();
            return;
        }
        if ("HOME".equals(cardName) && homePanel != null) {
            homePanel.refreshRooms();
        }
        cardLayout.show(mainContainer, cardName);
    }

    public void showBookCard(String roomNumber) {
        if (!AuthSession.hasAnyRole(EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER)) {
            UiMessages.showWarning(this, "Only receptionist or manager can create bookings.");
            return;
        }
        if (bookView != null) {
            bookView.setSelectedRoom(roomNumber);
        }
        showCard("BOOK");
    }

    public void showCheckoutCard(Long bookingId) {
        if (!AuthSession.hasAnyRole(EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER)) {
            UiMessages.showWarning(this, "Only receptionist or manager can process checkout.");
            return;
        }
        if (checkOutView != null) {
            checkOutView.setBookingId(bookingId);
        }
        showCard("CHECKOUT");
    }

    public void showAddServiceCard(Long bookingId) {
        if (!AuthSession.hasAnyRole(EmployeeRoles.SERVICE_STAFF, EmployeeRoles.MANAGER)) {
            UiMessages.showWarning(this, "Only service staff or manager can add services.");
            return;
        }
        if (addServiceView != null) {
            addServiceView.setBookingId(bookingId);
        }
        showCard("ADD_SERVICE");
    }

    private boolean promptLogin() {
        LoginDialog dialog = new LoginDialog(this, employeeController);
        dialog.setVisible(true);
        if (dialog.getAuthenticatedEmployee() == null) {
            return false;
        }
        AuthSession.login(dialog.getAuthenticatedEmployee());
        return true;
    }

    private void handleLogout() {
        AuthSession.logout();
        setVisible(false);
        if (!promptLogin()) {
            dispose();
            System.exit(0);
            return;
        }
        applyPermissions();
        setVisible(true);
        showDefaultCardForRole();
    }

    private void applyPermissions() {
        if (currentUserLabel != null && AuthSession.getCurrentEmployee() != null) {
            currentUserLabel.setText(
                    AuthSession.getCurrentEmployee().getFullName()
                            + " | "
                            + EmployeeRoles.toDisplayName(AuthSession.getCurrentEmployee().getRole())
            );
        }
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean allowed = isCardAllowed(entry.getKey());
            entry.getValue().setEnabled(allowed);
            entry.getValue().setToolTipText(allowed ? null : "This section is not available for your role.");
        }
        refreshHomeRooms();
    }

    private boolean isCardAllowed(String cardName) {
        if (cardName == null) {
            return true;
        }
        if ("HOME".equals(cardName)) {
            return !AuthSession.hasAnyRole(EmployeeRoles.SERVICE_STAFF)
                    || AuthSession.hasAnyRole(EmployeeRoles.MANAGER);
        }
        return switch (cardName) {
            case "BOOK", "CHECKOUT", "CUSTOMER" -> AuthSession.hasAnyRole(EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
            case "ADD_SERVICE" -> AuthSession.hasAnyRole(EmployeeRoles.SERVICE_STAFF, EmployeeRoles.MANAGER);
            case "SERVICE" -> AuthSession.hasAnyRole(EmployeeRoles.SERVICE_STAFF, EmployeeRoles.MANAGER);
            case "ROOMS", "EMPLOYEE", "INVOICES" -> AuthSession.hasAnyRole(EmployeeRoles.MANAGER);
            default -> true;
        };
    }

    private void showDefaultCardForRole() {
        if (AuthSession.hasAnyRole(EmployeeRoles.SERVICE_STAFF)) {
            showCard("SERVICE");
            return;
        }
        if (AuthSession.hasAnyRole(EmployeeRoles.RECEPTIONIST)) {
            showCard("BOOK");
            return;
        }
        showCard("HOME");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
