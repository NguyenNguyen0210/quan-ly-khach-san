package com.hotel.view;

import com.hotel.controller.BookingController;
import com.hotel.controller.ServiceCatalogController;
import com.hotel.controller.ServiceUsageController;
import com.hotel.entity.Booking;
import com.hotel.entity.Service;
import com.hotel.entity.ServiceUsage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AddServiceView extends JPanel {
    private final ServiceUsageController usageController;
    private final ServiceCatalogController catalogController;
    private final BookingController bookingController;

    private final JTextField txtBookingId;
    private final JLabel lblRoomNumber;
    private final JComboBox<String> serviceCombo;
    private final JTextField txtQuantity;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd;
    private final JLabel lblStatus;

    public AddServiceView(ServiceUsageController usageController, ServiceCatalogController catalogController, BookingController bookingController) {
        this.usageController = usageController;
        this.catalogController = catalogController;
        this.bookingController = bookingController;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);

        JPanel inputPanel = UiStyles.createCardPanel(new GridBagLayout(), 22, 22, 22, 22);
        inputPanel.setPreferredSize(new Dimension(340, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtBookingId = new JTextField(15);
        lblRoomNumber = new JLabel("Room: -");
        serviceCombo = new JComboBox<>();
        txtQuantity = new JTextField(15);
        UiStyles.styleTextField(txtBookingId);
        UiStyles.styleTextField(txtQuantity);
        UiStyles.styleComboBox(serviceCombo);
        lblRoomNumber.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRoomNumber.setForeground(UiStyles.TEXT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(UiStyles.createTitle("Add Service"), gbc);

        gbc.gridy = 1;
        inputPanel.add(UiStyles.createHint("Attach services to the selected booking and review current usage."), gbc);

        addField(inputPanel, gbc, 2, "Booking Id", txtBookingId);

        gbc.gridy = 4;
        gbc.insets = new Insets(4, 8, 8, 8);
        inputPanel.add(lblRoomNumber, gbc);

        addField(inputPanel, gbc, 6, "Service", serviceCombo);
        addField(inputPanel, gbc, 8, "Quantity", txtQuantity);

        btnAdd = UiStyles.createButton("ADD SERVICE", UiStyles.PRIMARY);

        gbc.gridy = 10;
        gbc.insets = new Insets(18, 8, 0, 8);
        inputPanel.add(btnAdd, gbc);

        lblStatus = UiStyles.createStatusLabel();
        gbc.gridy = 11;
        gbc.insets = new Insets(12, 8, 0, 8);
        inputPanel.add(lblStatus, gbc);

        String[] columns = {"Id", "Service Name", "Price", "Quantity", "Amount"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        UiStyles.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = UiStyles.wrapTable(table, "Current Services");

        contentPanel.add(inputPanel, BorderLayout.WEST);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        txtBookingId.addActionListener(e -> refreshTableFromBookingField());
        btnAdd.addActionListener(e -> handleAddService());
        attachValidationListeners();

        loadServices();
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

    private void loadServices() {
        serviceCombo.removeAllItems();
        List<Service> services = catalogController.getAllServices();
        for (Service service : services) {
            serviceCombo.addItem(service.getName());
        }
    }

    private void handleAddService() {
        try {
            String bookingText = txtBookingId.getText().trim();
            String serviceName = (String) serviceCombo.getSelectedItem();
            String quantityText = txtQuantity.getText().trim();

            if (bookingText.isEmpty() || serviceName == null || quantityText.isEmpty()) {
                UiStyles.setStatus(lblStatus, "Booking id, service and quantity are required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateForm();
                return;
            }

            Long bookingId = Long.parseLong(bookingText);
            int quantity = Integer.parseInt(quantityText);
            Service service = catalogController.getServiceByName(serviceName);
            if (service == null) {
                UiStyles.setStatus(lblStatus, "Selected service was not found.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                return;
            }

            usageController.addServiceUsage(bookingId, service.getId(), quantity);
            refreshTable(bookingId);
            txtQuantity.setText("");
            UiStyles.setStatus(lblStatus, "Service added to booking successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (NumberFormatException ex) {
            UiStyles.setStatus(lblStatus, "Booking id and quantity must be numeric.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateForm();
        } catch (Exception ex) {
            String rawMessage = ex.getMessage();
            if (rawMessage == null || rawMessage.isBlank()) {
                rawMessage = ex.toString();
            }
            String normalized = UiMessages.normalizeMessage(rawMessage);
            UiStyles.setStatus(lblStatus, normalized, UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
            UiMessages.showError(this, normalized);
        }
    }

    private void refreshTable(Long bookingId) {
        model.setRowCount(0);
        if (bookingId == null) {
            return;
        }
        List<ServiceUsage> usages = usageController.getServiceUsageByBooking(bookingId);
        for (ServiceUsage usage : usages) {
            Service service = usage.getService();
            if (service == null) {
                continue;
            }
            double amount = service.getPrice() * usage.getQuantity();
            model.addRow(new Object[]{usage.getId(), service.getName(), service.getPrice(), usage.getQuantity(), amount});
        }
    }

    public void setBookingId(Long bookingId) {
        if (bookingId == null) {
            txtBookingId.setText("");
            txtBookingId.setEditable(true);
            lblRoomNumber.setText("Room: -");
            model.setRowCount(0);
            validateForm();
            return;
        }
        txtBookingId.setText(String.valueOf(bookingId));
        txtBookingId.setEditable(false);
        Booking booking = bookingController.getBookingById(bookingId);
        if (booking != null && booking.getRoom() != null) {
            lblRoomNumber.setText("Room: " + booking.getRoom().getRoomNumber());
        } else {
            lblRoomNumber.setText("Room: -");
        }
        refreshTable(bookingId);
        validateForm();
    }

    private void refreshTableFromBookingField() {
        try {
            String bookingText = txtBookingId.getText().trim();
            if (bookingText.isEmpty()) {
                return;
            }
            Long bookingId = Long.parseLong(bookingText);
            setBookingId(bookingId);
        } catch (NumberFormatException ignored) {
        }
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
        txtBookingId.getDocument().addDocumentListener(listener);
        txtQuantity.getDocument().addDocumentListener(listener);
        serviceCombo.addActionListener(e -> validateForm());
    }

    private void validateForm() {
        boolean hasBookingId = !txtBookingId.getText().trim().isEmpty();
        boolean hasQuantity = !txtQuantity.getText().trim().isEmpty();
        boolean hasService = serviceCombo.getSelectedItem() != null;
        boolean numericQuantity = true;
        if (hasQuantity) {
            try {
                numericQuantity = Integer.parseInt(txtQuantity.getText().trim()) > 0;
            } catch (Exception ex) {
                numericQuantity = false;
            }
        }

        UiStyles.setFieldValidation(txtBookingId, hasBookingId);
        UiStyles.setFieldValidation(txtQuantity, hasQuantity && numericQuantity);
        UiStyles.setFieldValidation(serviceCombo, hasService);

        boolean valid = hasBookingId && hasQuantity && hasService && numericQuantity;
        btnAdd.setEnabled(valid);
        if (!valid) {
            UiStyles.setStatus(lblStatus, "Enter booking id, service and quantity to add service.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        }
    }
}
