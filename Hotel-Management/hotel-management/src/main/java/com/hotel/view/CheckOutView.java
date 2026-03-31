package com.hotel.view;

import com.hotel.controller.BookingController;
import com.hotel.controller.InvoiceController;
import com.hotel.controller.PaymentController;
import com.hotel.controller.ServiceUsageController;
import com.hotel.entity.Booking;
import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceDetail;
import com.hotel.entity.ServiceUsage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CheckOutView extends JPanel {
    private final InvoiceController invoiceController;
    private final PaymentController paymentController;
    private final BookingController bookingController;
    private final ServiceUsageController serviceUsageController;
    private final Runnable onCheckoutCompleted;

    private Booking currentBooking;
    private final JTextField txtBookingId;
    private final JLabel lblRoomNumber;
    private final JLabel lblInvoiceId;
    private final JLabel lblInvoiceStatus;
    private final JLabel lblTotal;
    private final JLabel lblPaid;
    private final JLabel lblOutstanding;
    private final JTextField txtPaymentAmount;
    private final JComboBox<String> paymentMethodCombo;
    private final JButton btnCreate;
    private final JButton btnPay;
    private final JButton btnCheckout;
    private final DefaultTableModel serviceTableModel;
    private final JTable tblServiceUsage;
    private final DefaultTableModel invoiceTableModel;
    private final JTable tblInvoices;
    private final JLabel lblStatus;

    private Invoice currentInvoice;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CheckOutView(InvoiceController invoiceController, PaymentController paymentController, BookingController bookingController, ServiceUsageController serviceUsageController, Runnable onCheckoutCompleted) {
        this.invoiceController = invoiceController;
        this.paymentController = paymentController;
        this.bookingController = bookingController;
        this.serviceUsageController = serviceUsageController;
        this.onCheckoutCompleted = onCheckoutCompleted;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);

        JPanel infoPanel = UiStyles.createCardPanel(new GridBagLayout(), 22, 22, 22, 22);
        infoPanel.setPreferredSize(new Dimension(360, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtBookingId = new JTextField();
        txtPaymentAmount = new JTextField();
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card", "Transfer"});
        UiStyles.styleTextField(txtBookingId);
        UiStyles.styleTextField(txtPaymentAmount);
        UiStyles.styleComboBox(paymentMethodCombo);

        lblRoomNumber = createInfoLabel("Room: -");
        lblInvoiceId = createInfoLabel("Invoice: -");
        lblInvoiceStatus = createInfoLabel("Status: -");
        lblTotal = createInfoLabel("Total: 0.00");
        lblPaid = createInfoLabel("Paid: 0.00");
        lblOutstanding = createInfoLabel("Outstanding: 0.00");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        infoPanel.add(UiStyles.createTitle("Check Out"), gbc);

        gbc.gridy = 1;
        infoPanel.add(UiStyles.createHint("Create invoice, receive payment and complete checkout."), gbc);

        addField(infoPanel, gbc, 2, "Booking Id", txtBookingId);

        gbc.gridy = 4;
        gbc.insets = new Insets(8, 8, 4, 8);
        infoPanel.add(lblRoomNumber, gbc);
        gbc.gridy = 5;
        infoPanel.add(lblInvoiceId, gbc);
        gbc.gridy = 6;
        infoPanel.add(lblInvoiceStatus, gbc);
        gbc.gridy = 7;
        infoPanel.add(lblTotal, gbc);
        gbc.gridy = 8;
        infoPanel.add(lblPaid, gbc);
        gbc.gridy = 9;
        infoPanel.add(lblOutstanding, gbc);

        addField(infoPanel, gbc, 11, "Payment Amount", txtPaymentAmount);
        addField(infoPanel, gbc, 13, "Payment Method", paymentMethodCombo);

        btnCreate = UiStyles.createButton("CREATE INVOICE", UiStyles.SUCCESS);
        btnPay = UiStyles.createButton("PAY", UiStyles.PRIMARY);
        btnCheckout = UiStyles.createButton("CHECK OUT", UiStyles.DANGER);

        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(btnCreate);
        actionPanel.add(btnPay);
        actionPanel.add(btnCheckout);

        gbc.gridy = 15;
        gbc.insets = new Insets(18, 8, 0, 8);
        infoPanel.add(actionPanel, gbc);

        lblStatus = UiStyles.createStatusLabel();
        gbc.gridy = 16;
        gbc.insets = new Insets(12, 8, 0, 8);
        infoPanel.add(lblStatus, gbc);

        serviceTableModel = new DefaultTableModel(new String[]{"Service", "Quantity", "Price", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblServiceUsage = new JTable(serviceTableModel);
        UiStyles.styleTable(tblServiceUsage);
        tblServiceUsage.setFillsViewportHeight(true);

        invoiceTableModel = new DefaultTableModel(new String[]{"Invoice ID", "Created", "Total", "Paid", "Outstanding"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInvoices = new JTable(invoiceTableModel);
        UiStyles.styleTable(tblInvoices);
        tblInvoices.setFillsViewportHeight(true);
        tblInvoices.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblInvoices.getSelectedRow();
                if (selectedRow < 0) {
                    return;
                }
                Long invoiceId = (Long) invoiceTableModel.getValueAt(selectedRow, 0);
                showInvoiceDialog(invoiceId);
            }
        });

        JScrollPane serviceScrollPane = UiStyles.wrapTable(tblServiceUsage, "Service Usage");
        JScrollPane invoiceScrollPane = UiStyles.wrapTable(tblInvoices, "Invoice List");

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, serviceScrollPane, invoiceScrollPane);
        rightSplitPane.setResizeWeight(0.55);
        rightSplitPane.setBorder(null);
        rightSplitPane.setOpaque(false);

        contentPanel.add(infoPanel, BorderLayout.WEST);
        contentPanel.add(rightSplitPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        btnCreate.addActionListener(e -> handleCreateInvoice());
        btnPay.addActionListener(e -> handlePayment());
        btnCheckout.addActionListener(e -> handleCheckOut());
        attachValidationListeners();
        validateFormState();
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(UiStyles.PANEL_ALT);
        label.setForeground(UiStyles.TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyles.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return label;
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

    private void handleCreateInvoice() {
        String bookingText = txtBookingId.getText().trim();
        if (bookingText.isEmpty()) {
            UiStyles.setStatus(lblStatus, "Booking id is required before creating an invoice.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateFormState();
            return;
        }
        try {
            long bookingId = Long.parseLong(bookingText);
            currentInvoice = invoiceController.createInvoiceForBooking(bookingId);
            currentBooking = bookingController.getBookingById(bookingId);
            if (currentBooking != null && currentBooking.getRoom() != null) {
                lblRoomNumber.setText("Room: " + currentBooking.getRoom().getRoomNumber());
            } else {
                lblRoomNumber.setText("Room: -");
            }
            updateInvoiceDetails();
            refreshServiceUsage(bookingId);
            refreshInvoiceList(bookingId);
            UiStyles.setStatus(lblStatus, "Invoice created successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
            validateFormState();
        } catch (NumberFormatException ex) {
            UiStyles.setStatus(lblStatus, "Booking id must be numeric.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateFormState();
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handlePayment() {
        if (currentInvoice == null) {
            UiStyles.setStatus(lblStatus, "Create or select an invoice first.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        try {
            String amountText = txtPaymentAmount.getText().trim();
            String method = (String) paymentMethodCombo.getSelectedItem();
            double amount = Double.parseDouble(amountText);
            paymentController.processPayment(currentInvoice.getId(), amount, method);
            currentInvoice = invoiceController.getInvoiceById(currentInvoice.getId());
            txtPaymentAmount.setText("");
            updateInvoiceDetails();
            Long bookingId = getEnteredBookingId();
            refreshInvoiceList(bookingId);
            UiStyles.setStatus(lblStatus, "Payment processed successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
            validateFormState();
        } catch (NumberFormatException ex) {
            UiStyles.setStatus(lblStatus, "Payment amount must be numeric.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateFormState();
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handleCheckOut() {
        if (currentInvoice == null) {
            UiStyles.setStatus(lblStatus, "Create or select an invoice first.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        double outstanding = paymentController.getOutstandingAmount(currentInvoice.getId());
        if (outstanding > 0) {
            UiStyles.setStatus(lblStatus, "Invoice must be fully paid before checkout.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        if (currentInvoice.getBooking() == null || currentInvoice.getBooking().getId() == null) {
            UiStyles.setStatus(lblStatus, "Booking information is missing.", UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
            return;
        }
        if (!UiMessages.confirm(this, "Confirm Check-out", "All payments are complete. Do you want to finish check-out for this booking?")) {
            return;
        }
        try {
            bookingController.checkOutAndCloseInvoice(currentInvoice.getBooking().getId(), currentInvoice.getId());
            UiStyles.setStatus(lblStatus, "Check-out completed successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
            clearForm();
            if (onCheckoutCompleted != null) {
                onCheckoutCompleted.run();
            }
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    public void setBookingId(Long bookingId) {
        if (bookingId == null) {
            return;
        }
        txtBookingId.setText(String.valueOf(bookingId));
        currentBooking = bookingController.getBookingById(bookingId);
        if (currentBooking != null && currentBooking.getRoom() != null) {
            lblRoomNumber.setText("Room: " + currentBooking.getRoom().getRoomNumber());
        } else {
            lblRoomNumber.setText("Room: -");
        }
        List<Invoice> existingInvoices = invoiceController.getInvoicesByBooking(bookingId);
        currentInvoice = existingInvoices.isEmpty() ? null : existingInvoices.get(0);
        txtPaymentAmount.setText("");
        updateInvoiceDetails();
        refreshServiceUsage(bookingId);
        refreshInvoiceList(bookingId);
        validateFormState();
    }

    private void refreshServiceUsage(Long bookingId) {
        serviceTableModel.setRowCount(0);
        if (bookingId == null) {
            return;
        }
        List<ServiceUsage> usages = serviceUsageController.getServiceUsageByBooking(bookingId);
        for (ServiceUsage usage : usages) {
            if (usage.getService() == null) {
                continue;
            }
            String serviceName = usage.getService().getName();
            int quantity = usage.getQuantity() == null ? 0 : usage.getQuantity();
            double price = usage.getService().getPrice() == null ? 0.0 : usage.getService().getPrice();
            double total = price * quantity;
            serviceTableModel.addRow(new Object[]{serviceName, quantity, String.format("%.2f", price), String.format("%.2f", total)});
        }
    }

    private void refreshInvoiceList(Long bookingId) {
        invoiceTableModel.setRowCount(0);
        if (bookingId == null) {
            return;
        }
        List<Invoice> invoices = invoiceController.getInvoicesByBooking(bookingId);
        for (Invoice invoice : invoices) {
            double total = invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount();
            double paid = paymentController.getPaidAmount(invoice.getId());
            double outstanding = paymentController.getOutstandingAmount(invoice.getId());
            String created = invoice.getCreatedDate() == null ? "-" : invoice.getCreatedDate().format(dateTimeFormatter);
            invoiceTableModel.addRow(new Object[]{
                    invoice.getId(),
                    created,
                    String.format("%.2f", total),
                    String.format("%.2f", paid),
                    String.format("%.2f", outstanding)
            });
        }
    }

    private void showInvoiceDialog(Long invoiceId) {
        if (invoiceId == null) {
            return;
        }

        Invoice invoice = invoiceController.getInvoiceById(invoiceId);
        if (invoice == null) {
            UiMessages.showError(this, "Invoice not found.");
            return;
        }

        double total = invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount();
        double paid = paymentController.getPaidAmount(invoiceId);
        double outstanding = paymentController.getOutstandingAmount(invoiceId);

        JPanel dialogPanel = new JPanel(new BorderLayout(12, 12));
        dialogPanel.setBackground(UiStyles.BACKGROUND);

        JPanel summaryPanel = UiStyles.createCardPanel(new GridLayout(4, 1, 0, 6), 14, 14, 14, 14);
        summaryPanel.add(new JLabel("Invoice ID: " + invoiceId));
        summaryPanel.add(new JLabel("Created: " + (invoice.getCreatedDate() == null ? "-" : invoice.getCreatedDate().format(dateTimeFormatter))));
        summaryPanel.add(new JLabel(String.format("Total: %.2f | Paid: %.2f", total, paid)));
        summaryPanel.add(new JLabel(String.format("Outstanding: %.2f", outstanding)));

        DefaultTableModel detailModel = new DefaultTableModel(new String[]{"Description", "Quantity", "Unit Price", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable detailTable = new JTable(detailModel);
        UiStyles.styleTable(detailTable);

        List<InvoiceDetail> details = invoiceController.getInvoiceDetails(invoiceId);
        for (InvoiceDetail detail : details) {
            detailModel.addRow(new Object[]{
                    detail.getDescription() == null ? "-" : detail.getDescription(),
                    detail.getQuantity() == null ? 0 : detail.getQuantity(),
                    String.format("%.2f", detail.getUnitPrice() == null ? 0.0 : detail.getUnitPrice()),
                    String.format("%.2f", detail.getAmount() == null ? 0.0 : detail.getAmount())
            });
        }

        dialogPanel.add(summaryPanel, BorderLayout.NORTH);
        dialogPanel.add(UiStyles.wrapTable(detailTable, "Invoice Details"), BorderLayout.CENTER);
        dialogPanel.setPreferredSize(new Dimension(620, 420));

        JOptionPane.showMessageDialog(this, dialogPanel, "Invoice Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        currentInvoice = null;
        txtBookingId.setText("");
        txtPaymentAmount.setText("");
        lblRoomNumber.setText("Room: -");
        lblInvoiceId.setText("Invoice: -");
        lblInvoiceStatus.setText("Status: -");
        serviceTableModel.setRowCount(0);
        invoiceTableModel.setRowCount(0);
        updateInvoiceDetails();
        validateFormState();
    }

    private void updateInvoiceDetails() {
        if (currentInvoice == null) {
            lblInvoiceId.setText("Invoice: -");
            lblInvoiceStatus.setText("Status: Draft");
            Long bookingId = getEnteredBookingId();
            double estimatedTotal = bookingId == null ? 0.0 : invoiceController.calculateInvoiceAmount(bookingId);
            lblTotal.setText(String.format("Total: %.2f", estimatedTotal));
            lblPaid.setText("Paid: 0.00");
            lblOutstanding.setText(String.format("Outstanding: %.2f", estimatedTotal));
            btnCheckout.setEnabled(false);
            return;
        }
        double total = currentInvoice.getTotalAmount() == null ? 0.0 : currentInvoice.getTotalAmount();
        double paid = paymentController.getPaidAmount(currentInvoice.getId());
        double outstanding = paymentController.getOutstandingAmount(currentInvoice.getId());
        lblInvoiceId.setText("Invoice: " + currentInvoice.getId());
        lblInvoiceStatus.setText("Status: " + formatInvoiceStatus(currentInvoice.getStatus()));
        lblTotal.setText(String.format("Total: %.2f", total));
        lblPaid.setText(String.format("Paid: %.2f", paid));
        lblOutstanding.setText(String.format("Outstanding: %.2f", outstanding));
        btnCheckout.setEnabled(outstanding <= 0.0);
    }

    private String formatInvoiceStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Draft";
        }
        return switch (status.trim().toUpperCase()) {
            case "ISSUED" -> "Issued";
            case "PARTIALLY_PAID" -> "Partially Paid";
            case "PAID" -> "Paid";
            case "CLOSED" -> "Closed";
            default -> status.replace('_', ' ');
        };
    }

    private Long getEnteredBookingId() {
        String bookingText = txtBookingId.getText().trim();
        if (bookingText.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(bookingText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void attachValidationListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateFormState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateFormState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateFormState();
            }
        };
        txtBookingId.getDocument().addDocumentListener(listener);
        txtPaymentAmount.getDocument().addDocumentListener(listener);
        paymentMethodCombo.addActionListener(e -> validateFormState());
    }

    private void validateFormState() {
        Long bookingId = getEnteredBookingId();
        boolean hasBookingId = bookingId != null;
        UiStyles.setFieldValidation(txtBookingId, hasBookingId || txtBookingId.getText().trim().isEmpty());
        boolean bookingAllowsInvoice = false;
        if (hasBookingId) {
            try {
                Booking booking = bookingController.getBookingById(bookingId);
                bookingAllowsInvoice = booking != null && "CHECKED_IN".equalsIgnoreCase(booking.getStatus());
            } catch (Exception ignored) {
                bookingAllowsInvoice = false;
            }
        }
        btnCreate.setEnabled(bookingAllowsInvoice);

        boolean hasInvoice = currentInvoice != null && currentInvoice.getId() != null;
        boolean invoiceAcceptsPayment = hasInvoice
                && !("PAID".equalsIgnoreCase(currentInvoice.getStatus()) || "CLOSED".equalsIgnoreCase(currentInvoice.getStatus()));
        boolean validAmount = false;
        String paymentText = txtPaymentAmount.getText().trim();
        if (!paymentText.isEmpty()) {
            try {
                validAmount = Double.parseDouble(paymentText) > 0;
            } catch (Exception ignored) {
                validAmount = false;
            }
        }

        UiStyles.setFieldValidation(txtPaymentAmount, paymentText.isEmpty() || validAmount);
        UiStyles.setFieldValidation(paymentMethodCombo, paymentMethodCombo.getSelectedItem() != null);
        btnPay.setEnabled(invoiceAcceptsPayment && validAmount);

        if (!hasInvoice) {
            btnCheckout.setEnabled(false);
        }

        if (!hasBookingId) {
            UiStyles.setStatus(lblStatus, "Enter a booking id to start checkout.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else if (!bookingAllowsInvoice && currentInvoice == null) {
            UiStyles.setStatus(lblStatus, "Only checked-in bookings can create invoices.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else if (!hasInvoice) {
            UiStyles.setStatus(lblStatus, "Create an invoice to continue.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else if (!validAmount && !paymentText.isEmpty()) {
            UiStyles.setStatus(lblStatus, "Payment amount must be a positive number.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
        }
    }
}
