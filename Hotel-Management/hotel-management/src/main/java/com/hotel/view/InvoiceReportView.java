package com.hotel.view;

import com.hotel.controller.InvoiceController;
import com.hotel.controller.PaymentController;
import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceDetail;
import com.hotel.entity.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class InvoiceReportView extends JPanel {
    private final InvoiceController invoiceController;
    private final PaymentController paymentController;

    private final JTextField txtFromDate;
    private final JTextField txtToDate;
    private final JLabel lblInvoiceCount;
    private final JLabel lblRevenue;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public InvoiceReportView(InvoiceController invoiceController, PaymentController paymentController) {
        this.invoiceController = invoiceController;
        this.paymentController = paymentController;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel topPanel = UiStyles.createCardPanel(new BorderLayout(16, 16), 20, 20, 20, 20);

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        header.add(UiStyles.createTitle("Invoice Report"), BorderLayout.NORTH);
        header.add(UiStyles.createHint("View invoice history, filter by date, and inspect invoice details."), BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtFromDate = new JTextField(12);
        txtToDate = new JTextField(12);
        UiStyles.styleTextField(txtFromDate);
        UiStyles.styleTextField(txtToDate);
        UiStyles.enableDatePicker(txtFromDate);
        UiStyles.enableDatePicker(txtToDate);

        JButton btnFilter = UiStyles.createButton("FILTER", UiStyles.PRIMARY);
        JButton btnReset = UiStyles.createButton("RESET", UiStyles.SUCCESS);
        JButton btnView = UiStyles.createButton("VIEW DETAIL", UiStyles.DANGER);

        addField(filterPanel, gbc, 0, "From Date", txtFromDate);
        addField(filterPanel, gbc, 2, "To Date", txtToDate);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(btnFilter);
        actionPanel.add(btnReset);
        actionPanel.add(btnView);
        filterPanel.add(actionPanel, gbc);

        lblInvoiceCount = createStatLabel("Invoices: 0");
        lblRevenue = createStatLabel("Revenue: 0.00");

        JPanel statPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        statPanel.setOpaque(false);
        statPanel.add(lblInvoiceCount);
        statPanel.add(lblRevenue);

        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(statPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{
                "Invoice ID", "Created", "Booking ID", "Customer", "Room", "Status", "Processed By", "Total", "Paid", "Outstanding"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UiStyles.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(topPanel, BorderLayout.NORTH);
        add(UiStyles.wrapTable(table, "Invoice History"), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> resetFilter());
        btnView.addActionListener(e -> openSelectedInvoice());

        refreshData();
    }

    public void refreshData() {
        refreshTable(invoiceController.getAllInvoices());
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 8, 2, 8);
        JLabel title = UiStyles.createSectionTitle(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(title, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 8, 8, 8);
        panel.add(field, gbc);
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(UiStyles.PANEL_ALT);
        label.setForeground(UiStyles.TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyles.BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        return label;
    }

    private void applyFilter() {
        try {
            String fromText = txtFromDate.getText().trim();
            String toText = txtToDate.getText().trim();
            if (fromText.isEmpty() || toText.isEmpty()) {
                refreshTable(invoiceController.getAllInvoices());
                return;
            }

            LocalDateTime from = LocalDate.parse(fromText, dateFormatter).atStartOfDay();
            LocalDateTime to = LocalDate.parse(toText, dateFormatter).atTime(23, 59, 59);
            refreshTable(invoiceController.getInvoicesByDateRange(from, to));
        } catch (DateTimeParseException ex) {
            UiMessages.showWarning(this, "Date must use format yyyy-MM-dd.");
        }
    }

    private void resetFilter() {
        txtFromDate.setText("");
        txtToDate.setText("");
        refreshTable(invoiceController.getAllInvoices());
    }

    private void refreshTable(List<Invoice> invoices) {
        tableModel.setRowCount(0);
        double revenue = 0.0;
        for (Invoice invoice : invoices) {
            Long invoiceId = invoice.getId();
            double total = invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount();
            double paid = paymentController.getPaidAmount(invoiceId);
            double outstanding = paymentController.getOutstandingAmount(invoiceId);
            Payment latestPayment = paymentController.getLatestPaymentByInvoice(invoiceId);
            revenue += paid;

            String created = invoice.getCreatedDate() == null ? "-" : invoice.getCreatedDate().format(dateTimeFormatter);
            Long bookingId = invoice.getBooking() == null ? null : invoice.getBooking().getId();
            String customer = invoice.getBooking() == null || invoice.getBooking().getCustomer() == null
                    ? "-"
                    : invoice.getBooking().getCustomer().getFullName();
            String room = invoice.getBooking() == null || invoice.getBooking().getRoom() == null
                    ? "-"
                    : invoice.getBooking().getRoom().getRoomNumber();
            String status = formatInvoiceStatus(invoice.getStatus());
            String processedBy = latestPayment == null || latestPayment.getProcessedBy() == null
                    ? "-"
                    : latestPayment.getProcessedBy().getFullName();

            tableModel.addRow(new Object[]{
                    invoiceId,
                    created,
                    bookingId,
                    customer,
                    room,
                    status,
                    processedBy,
                    String.format("%.2f", total),
                    String.format("%.2f", paid),
                    String.format("%.2f", outstanding)
            });
        }
        lblInvoiceCount.setText("Invoices: " + invoices.size());
        lblRevenue.setText(String.format("Revenue: %.2f", revenue));
    }

    private void openSelectedInvoice() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            UiMessages.showWarning(this, "Please select an invoice to view.");
            return;
        }

        Long invoiceId = (Long) tableModel.getValueAt(selectedRow, 0);
        Invoice invoice = invoiceController.getInvoiceById(invoiceId);
        if (invoice == null) {
            UiMessages.showError(this, "Invoice not found.");
            return;
        }

        double total = invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount();
        double paid = paymentController.getPaidAmount(invoiceId);
        double outstanding = paymentController.getOutstandingAmount(invoiceId);
        Payment latestPayment = paymentController.getLatestPaymentByInvoice(invoiceId);
        String processedBy = latestPayment == null || latestPayment.getProcessedBy() == null
                ? "-"
                : latestPayment.getProcessedBy().getFullName();

        JPanel dialogPanel = new JPanel(new BorderLayout(12, 12));
        dialogPanel.setBackground(UiStyles.BACKGROUND);

        JPanel summary = UiStyles.createCardPanel(new GridLayout(7, 1, 0, 6), 14, 14, 14, 14);
        summary.add(new JLabel("Invoice ID: " + invoiceId));
        summary.add(new JLabel("Created: " + (invoice.getCreatedDate() == null ? "-" : invoice.getCreatedDate().format(dateTimeFormatter))));
        summary.add(new JLabel("Booking ID: " + (invoice.getBooking() == null ? "-" : invoice.getBooking().getId())));
        summary.add(new JLabel("Status: " + formatInvoiceStatus(invoice.getStatus())));
        summary.add(new JLabel("Processed By: " + processedBy));
        summary.add(new JLabel(String.format("Total: %.2f | Paid: %.2f", total, paid)));
        summary.add(new JLabel(String.format("Outstanding: %.2f", outstanding)));

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

        dialogPanel.add(summary, BorderLayout.NORTH);
        dialogPanel.add(UiStyles.wrapTable(detailTable, "Invoice Details"), BorderLayout.CENTER);
        dialogPanel.setPreferredSize(new Dimension(700, 460));

        JOptionPane.showMessageDialog(this, dialogPanel, "Invoice Detail", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatInvoiceStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Issued";
        }
        return switch (status.trim().toUpperCase()) {
            case "ISSUED" -> "Issued";
            case "PARTIALLY_PAID" -> "Partially Paid";
            case "PAID" -> "Paid";
            case "CLOSED" -> "Closed";
            default -> status.replace('_', ' ');
        };
    }
}
