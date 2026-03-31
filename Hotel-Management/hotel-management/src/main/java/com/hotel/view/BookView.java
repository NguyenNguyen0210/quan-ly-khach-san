package com.hotel.view;

import com.hotel.Main;
import com.hotel.controller.BookingController;
import com.hotel.controller.CustomerController;
import com.hotel.controller.RoomController;
import com.hotel.entity.Booking;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookView extends JPanel {
    private static final int FORM_WIDTH = 360;
    private static final int INPUT_WIDTH = 280;
    private static final int INPUT_HEIGHT = 34;
    private static final int SUGGESTION_HEIGHT = 120;

    private final Main mainApp;
    private final BookingController bookingController;
    private final CustomerController customerController;
    private final RoomController roomController;

    private final JTextField txtName;
    private final JTextField txtPhone;
    private final JTextField txtIdCard;
    private final JTextField txtEmail;
    private final JTextField txtCheckIn;
    private final JTextField txtCheckOut;
    private final JComboBox<String> roomCombo;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd;
    private final JLabel lblStatus;
    private final JPopupMenu customerSuggestionPopup;
    private final DefaultListModel<Customer> customerSuggestionModel;
    private final JList<Customer> customerSuggestionList;
    private final JScrollPane customerSuggestionScrollPane;
    private final JPanel nameSuggestionHost;
    private final JPanel phoneSuggestionHost;
    private Customer selectedCustomer;
    private boolean customerSelectionInProgress;
    private JTextField lastSuggestionSourceField;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public BookView(Main mainApp, BookingController bookingController, CustomerController customerController, RoomController roomController) {
        this.mainApp = mainApp;
        this.bookingController = bookingController;
        this.customerController = customerController;
        this.roomController = roomController;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);

        JPanel leftPanel = UiStyles.createCardPanel(new GridBagLayout(), 22, 22, 22, 22);
        leftPanel.setPreferredSize(new Dimension(FORM_WIDTH, 0));
        leftPanel.setMinimumSize(new Dimension(FORM_WIDTH, 0));
        leftPanel.setMaximumSize(new Dimension(FORM_WIDTH, Integer.MAX_VALUE));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtName = new JTextField(15);
        txtPhone = new JTextField(15);
        txtIdCard = new JTextField(15);
        txtEmail = new JTextField(15);
        txtCheckIn = new JTextField(15);
        txtCheckOut = new JTextField(15);
        roomCombo = new JComboBox<>();
        UiStyles.styleTextField(txtName);
        UiStyles.styleTextField(txtPhone);
        UiStyles.styleTextField(txtIdCard);
        UiStyles.styleTextField(txtEmail);
        UiStyles.styleTextField(txtCheckIn);
        UiStyles.styleTextField(txtCheckOut);
        UiStyles.styleComboBox(roomCombo);
        lockFieldSize(txtName);
        lockFieldSize(txtPhone);
        lockFieldSize(txtIdCard);
        lockFieldSize(txtEmail);
        lockFieldSize(txtCheckIn);
        lockFieldSize(txtCheckOut);
        lockFieldSize(roomCombo);
        customerSuggestionPopup = new JPopupMenu();
        customerSuggestionModel = new DefaultListModel<>();
        customerSuggestionList = new JList<>(customerSuggestionModel);
        customerSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerSuggestionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer customer) {
                    label.setText(customer.getFullName() + " - " + customer.getPhone());
                }
                label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return label;
            }
        });
        customerSuggestionScrollPane = new JScrollPane(customerSuggestionList);
        customerSuggestionScrollPane.setBorder(BorderFactory.createLineBorder(UiStyles.BORDER, 1, true));
        customerSuggestionScrollPane.setPreferredSize(new Dimension(INPUT_WIDTH, SUGGESTION_HEIGHT));
        customerSuggestionScrollPane.setMinimumSize(new Dimension(INPUT_WIDTH, SUGGESTION_HEIGHT));
        customerSuggestionScrollPane.setMaximumSize(new Dimension(INPUT_WIDTH, SUGGESTION_HEIGHT));
        customerSuggestionScrollPane.setVisible(false);
        nameSuggestionHost = createSuggestionHost();
        phoneSuggestionHost = createSuggestionHost();
        customerSuggestionPopup.setBorder(BorderFactory.createEmptyBorder());
        customerSuggestionPopup.add(customerSuggestionScrollPane);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        leftPanel.add(UiStyles.createTitle("Booking"), gbc);

        gbc.gridy = 1;
        leftPanel.add(UiStyles.createHint("Create a reservation and assign an available room."), gbc);

        addField(leftPanel, gbc, 2, "Customer Name", txtName);
        addSuggestionArea(leftPanel, gbc, 4, nameSuggestionHost);
        addField(leftPanel, gbc, 6, "Phone", txtPhone);
        addSuggestionArea(leftPanel, gbc, 8, phoneSuggestionHost);
        addField(leftPanel, gbc, 10, "ID Card", txtIdCard);
        addField(leftPanel, gbc, 12, "Email", txtEmail);
        addField(leftPanel, gbc, 14, "Check-in date", txtCheckIn);
        addField(leftPanel, gbc, 16, "Check-out date", txtCheckOut);
        addField(leftPanel, gbc, 18, "Room", roomCombo);

        btnAdd = UiStyles.createButton("ADD BOOKING", UiStyles.SUCCESS);

        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 0, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        leftPanel.add(btnAdd, gbc);

        lblStatus = UiStyles.createStatusLabel();
        gbc.gridy = 21;
        gbc.insets = new Insets(12, 8, 0, 8);
        leftPanel.add(lblStatus, gbc);

        String[] columns = {"Id", "Room", "Customer", "Check-In", "Check-Out", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        UiStyles.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = UiStyles.wrapTable(table, "Bookings");

        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handlePlaceBooking());
        attachValidationListeners();
        attachCustomerSuggestions();

        loadRooms();
        refreshBookingTable();
        validateForm();
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component component) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 8, 2, 8);
        JLabel title = UiStyles.createSectionTitle(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(title, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 8, 8, 8);
        panel.add(component, gbc);
    }

    private JPanel createSuggestionHost() {
        JPanel host = new JPanel(new BorderLayout());
        host.setOpaque(false);
        host.setVisible(false);
        host.setPreferredSize(new Dimension(INPUT_WIDTH, SUGGESTION_HEIGHT));
        host.setMinimumSize(new Dimension(INPUT_WIDTH, SUGGESTION_HEIGHT));
        host.setMaximumSize(new Dimension(INPUT_WIDTH, SUGGESTION_HEIGHT));
        return host;
    }

    private void addSuggestionArea(JPanel panel, GridBagConstraints gbc, int row, JPanel host) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 8, 8);
        panel.add(host, gbc);
    }

    private void loadRooms() {
        roomCombo.removeAllItems();
        List<Room> rooms = roomController.getAllRooms();
        for (Room room : rooms) {
            String status = room.getStatus();
            if (status == null || !status.equalsIgnoreCase("MAINTENANCE")) {
                roomCombo.addItem(room.getRoomNumber());
            }
        }
        if (roomCombo.getItemCount() == 0) {
            roomCombo.addItem("No available rooms");
            roomCombo.setEnabled(false);
        } else {
            roomCombo.setEnabled(true);
        }
    }

    private void handlePlaceBooking() {
        try {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String idCard = txtIdCard.getText().trim();
            String email = txtEmail.getText().trim();
            String checkInText = txtCheckIn.getText().trim();
            String checkOutText = txtCheckOut.getText().trim();
            String roomNumber = (String) roomCombo.getSelectedItem();

            if (name.isEmpty() || phone.isEmpty() || idCard.isEmpty() || checkInText.isEmpty() || checkOutText.isEmpty() || roomNumber == null) {
                UiStyles.setStatus(lblStatus, "Please fill all required fields before creating a booking.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateForm();
                return;
            }

            LocalDate checkIn = parseDate(checkInText);
            LocalDate checkOut = parseDate(checkOutText);
            if (checkIn.isAfter(checkOut)) {
                UiStyles.setStatus(lblStatus, "Check-in date must be before or equal to check-out date.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateForm();
                return;
            }

            Customer customer = resolveSelectedOrExistingCustomer(idCard, phone);
            if (customer == null) {
                customer = new Customer();
                customer.setFullName(name);
                customer.setPhone(phone);
                customer.setEmail(email);
                customer.setIdCard(idCard);
                customerController.registerCustomer(customer);
            } else {
                populateCustomerFields(customer);
            }

            Room room = roomController.getRoomByNumber(roomNumber);
            if (room == null) {
                UiStyles.setStatus(lblStatus, "Selected room was not found. Please refresh and try again.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                return;
            }

            bookingController.placeBooking(customer.getId(), room.getId(), checkIn, checkOut);
            mainApp.refreshHomeRooms();
            loadRooms();
            refreshBookingTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Booking created successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (DateTimeParseException ex) {
            UiStyles.setStatus(lblStatus, "Dates must use the format YYYY-MM-DD.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateForm();
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private LocalDate parseDate(String text) {
        return LocalDate.parse(text, formatter);
    }

    private void refreshBookingTable() {
        model.setRowCount(0);
        List<Booking> bookings = bookingController.getAllBookings();
        for (Booking booking : bookings) {
            if ("CHECKED_OUT".equalsIgnoreCase(booking.getStatus())) {
                continue;
            }
            String roomValue = booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A";
            String customerValue = booking.getCustomer() != null ? booking.getCustomer().getFullName() : "N/A";
            model.addRow(new Object[]{booking.getId(), roomValue, customerValue, booking.getCheckInDate(), booking.getCheckOutDate(), booking.getStatus()});
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtPhone.setText("");
        txtIdCard.setText("");
        txtEmail.setText("");
        txtCheckIn.setText("");
        txtCheckOut.setText("");
        selectedCustomer = null;
        customerSuggestionPopup.setVisible(false);
        hideSuggestionHosts();
        validateForm();
    }

    public void setSelectedRoom(String roomNumber) {
        loadRooms();
        if (roomNumber != null && !roomNumber.isBlank()) {
            roomCombo.setSelectedItem(roomNumber);
        }
    }

    public void refreshData() {
        loadRooms();
        refreshBookingTable();
        validateForm();
    }

    private void attachValidationListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateForm();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateForm();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateForm();
            }
        };
        txtName.getDocument().addDocumentListener(listener);
        txtPhone.getDocument().addDocumentListener(listener);
        txtIdCard.getDocument().addDocumentListener(listener);
        txtEmail.getDocument().addDocumentListener(listener);
        txtCheckIn.getDocument().addDocumentListener(listener);
        txtCheckOut.getDocument().addDocumentListener(listener);
        roomCombo.addActionListener(e -> validateForm());
    }

    private void attachCustomerSuggestions() {
        DocumentListener suggestionListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (customerSelectionInProgress) {
                    return;
                }
                lastSuggestionSourceField = txtName;
                selectedCustomer = null;
                showCustomerSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (customerSelectionInProgress) {
                    return;
                }
                lastSuggestionSourceField = txtName;
                selectedCustomer = null;
                showCustomerSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (customerSelectionInProgress) {
                    return;
                }
                lastSuggestionSourceField = txtName;
                selectedCustomer = null;
                showCustomerSuggestions();
            }
        };
        txtName.getDocument().addDocumentListener(suggestionListener);
        txtPhone.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (customerSelectionInProgress) {
                    return;
                }
                lastSuggestionSourceField = txtPhone;
                selectedCustomer = null;
                showCustomerSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (customerSelectionInProgress) {
                    return;
                }
                lastSuggestionSourceField = txtPhone;
                selectedCustomer = null;
                showCustomerSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (customerSelectionInProgress) {
                    return;
                }
                lastSuggestionSourceField = txtPhone;
                selectedCustomer = null;
                showCustomerSuggestions();
            }
        });

        customerSuggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Customer customer = customerSuggestionList.getSelectedValue();
                if (customer != null) {
                    selectedCustomer = customer;
                    customerSelectionInProgress = true;
                    populateCustomerFields(customer);
                    customerSelectionInProgress = false;
                    customerSuggestionPopup.setVisible(false);
                    hideSuggestionHosts();
                    UiStyles.setStatus(lblStatus, "Existing customer selected. Information filled automatically.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
                    SwingUtilities.invokeLater(() -> {
                        JTextField focusField = lastSuggestionSourceField != null ? lastSuggestionSourceField : txtName;
                        focusField.requestFocusInWindow();
                        focusField.setCaretPosition(focusField.getText().length());
                        if (focusField == txtName) {
                            txtName.selectAll();
                        } else {
                            txtPhone.selectAll();
                        }
                    });
                }
            }
        });

        FocusAdapter hidePopupListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!txtName.hasFocus() && !txtPhone.hasFocus() && !customerSuggestionList.hasFocus()) {
                        customerSuggestionPopup.setVisible(false);
                        hideSuggestionHosts();
                    }
                });
            }
        };
        txtName.addFocusListener(hidePopupListener);
        txtPhone.addFocusListener(hidePopupListener);
        customerSuggestionList.addFocusListener(hidePopupListener);
    }

    private void showCustomerSuggestions() {
        String nameQuery = txtName.getText().trim();
        String phoneQuery = txtPhone.getText().trim();

        if ((nameQuery.isEmpty() && phoneQuery.isEmpty()) || selectedCustomerMatchesInput()) {
            customerSuggestionPopup.setVisible(false);
            hideSuggestionHosts();
            return;
        }

        List<Customer> matches = findMatchingCustomers(nameQuery, phoneQuery);
        customerSuggestionModel.clear();
        for (Customer customer : matches) {
            customerSuggestionModel.addElement(customer);
        }

        if (matches.isEmpty()) {
            customerSuggestionPopup.setVisible(false);
            hideSuggestionHosts();
            return;
        }

        customerSuggestionList.setVisibleRowCount(Math.min(matches.size(), 5));
        showSuggestionInActiveHost(!phoneQuery.isEmpty() && txtPhone.hasFocus() ? phoneSuggestionHost : nameSuggestionHost);
        revalidate();
        repaint();
    }

    private void showSuggestionInActiveHost(JPanel host) {
        JPanel targetHost = host == phoneSuggestionHost ? phoneSuggestionHost : nameSuggestionHost;
        JPanel otherHost = targetHost == phoneSuggestionHost ? nameSuggestionHost : phoneSuggestionHost;

        if (customerSuggestionScrollPane.getParent() != targetHost) {
            Container currentParent = customerSuggestionScrollPane.getParent();
            if (currentParent instanceof JPanel currentPanel) {
                currentPanel.remove(customerSuggestionScrollPane);
                currentPanel.revalidate();
                currentPanel.repaint();
            }
            targetHost.removeAll();
            targetHost.add(customerSuggestionScrollPane, BorderLayout.CENTER);
        }

        customerSuggestionScrollPane.setVisible(true);
        targetHost.setVisible(true);
        otherHost.setVisible(false);
        otherHost.removeAll();
        targetHost.revalidate();
        targetHost.repaint();
    }

    private void hideSuggestionHosts() {
        customerSuggestionScrollPane.setVisible(false);
        nameSuggestionHost.removeAll();
        phoneSuggestionHost.removeAll();
        nameSuggestionHost.setVisible(false);
        phoneSuggestionHost.setVisible(false);
        nameSuggestionHost.revalidate();
        phoneSuggestionHost.revalidate();
        nameSuggestionHost.repaint();
        phoneSuggestionHost.repaint();
    }

    private void lockFieldSize(JComponent component) {
        Dimension size = new Dimension(INPUT_WIDTH, INPUT_HEIGHT);
        component.setPreferredSize(size);
        component.setMinimumSize(size);
        component.setMaximumSize(size);
    }

    private List<Customer> findMatchingCustomers(String nameQuery, String phoneQuery) {
        List<Customer> matches = new ArrayList<>();
        String normalizedName = nameQuery.toLowerCase(Locale.ROOT);
        String normalizedPhone = phoneQuery.toLowerCase(Locale.ROOT);
        boolean phoneFocused = txtPhone.hasFocus() && !normalizedPhone.isEmpty();
        boolean nameFocused = txtName.hasFocus() && !normalizedName.isEmpty();

        for (Customer customer : customerController.getAllCustomers()) {
            String fullName = customer.getFullName() == null ? "" : customer.getFullName().toLowerCase(Locale.ROOT);
            String phone = customer.getPhone() == null ? "" : customer.getPhone().toLowerCase(Locale.ROOT);

            boolean matchName = !normalizedName.isEmpty() && fullName.contains(normalizedName);
            boolean matchPhone = !normalizedPhone.isEmpty() && phone.contains(normalizedPhone);

            boolean matched;
            if (phoneFocused) {
                matched = matchPhone;
            } else if (nameFocused) {
                matched = matchName;
            } else if (!normalizedPhone.isEmpty() && normalizedName.isEmpty()) {
                matched = matchPhone;
            } else if (!normalizedName.isEmpty() && normalizedPhone.isEmpty()) {
                matched = matchName;
            } else {
                matched = matchName || matchPhone;
            }

            if (matched) {
                matches.add(customer);
            }
            if (matches.size() >= 8) {
                break;
            }
        }
        return matches;
    }

    private boolean selectedCustomerMatchesInput() {
        if (selectedCustomer == null) {
            return false;
        }
        return txtName.getText().trim().equals(selectedCustomer.getFullName())
                && txtPhone.getText().trim().equals(selectedCustomer.getPhone());
    }

    private void populateCustomerFields(Customer customer) {
        if (customer == null) {
            return;
        }
        boolean previousSelectionState = customerSelectionInProgress;
        customerSelectionInProgress = true;
        try {
            txtName.setText(customer.getFullName() == null ? "" : customer.getFullName());
            txtPhone.setText(customer.getPhone() == null ? "" : customer.getPhone());
            txtIdCard.setText(customer.getIdCard() == null ? "" : customer.getIdCard());
            txtEmail.setText(customer.getEmail() == null ? "" : customer.getEmail());
            selectedCustomer = customer;
            validateForm();
        } finally {
            customerSelectionInProgress = previousSelectionState;
        }
    }

    private Customer resolveSelectedOrExistingCustomer(String idCard, String phone) {
        if (selectedCustomer != null && selectedCustomer.getId() != null) {
            Customer existingSelected = customerController.getCustomerById(selectedCustomer.getId());
            if (existingSelected != null) {
                return existingSelected;
            }
        }
        Customer customer = customerController.findCustomerByIdCard(idCard);
        if (customer != null) {
            return customer;
        }
        return customerController.findCustomerByPhone(phone);
    }

    private void validateForm() {
        boolean hasName = !txtName.getText().trim().isEmpty();
        boolean hasPhone = !txtPhone.getText().trim().isEmpty();
        boolean hasIdCard = !txtIdCard.getText().trim().isEmpty();
        boolean hasCheckIn = !txtCheckIn.getText().trim().isEmpty();
        boolean hasCheckOut = !txtCheckOut.getText().trim().isEmpty();
        boolean hasRoom = roomCombo.isEnabled() && roomCombo.getSelectedItem() != null && !"No available rooms".equals(roomCombo.getSelectedItem());

        UiStyles.setFieldValidation(txtName, hasName);
        UiStyles.setFieldValidation(txtPhone, hasPhone);
        UiStyles.setFieldValidation(txtIdCard, hasIdCard);
        UiStyles.setFieldValidation(txtCheckIn, hasCheckIn);
        UiStyles.setFieldValidation(txtCheckOut, hasCheckOut);
        UiStyles.setFieldValidation(roomCombo, hasRoom);

        boolean dateRangeValid = true;
        if (hasCheckIn && hasCheckOut) {
            try {
                LocalDate checkIn = parseDate(txtCheckIn.getText().trim());
                LocalDate checkOut = parseDate(txtCheckOut.getText().trim());
                dateRangeValid = !checkIn.isAfter(checkOut);
            } catch (Exception ignored) {
                dateRangeValid = false;
            }
        }
        if (hasCheckIn) {
            UiStyles.setFieldValidation(txtCheckIn, dateRangeValid);
        }
        if (hasCheckOut) {
            UiStyles.setFieldValidation(txtCheckOut, dateRangeValid);
        }

        boolean formValid = hasName && hasPhone && hasIdCard && hasCheckIn && hasCheckOut && hasRoom && dateRangeValid;
        btnAdd.setEnabled(formValid);

        if (!formValid) {
            UiStyles.setStatus(lblStatus, "Fill all required fields to enable booking.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        }
    }
}
