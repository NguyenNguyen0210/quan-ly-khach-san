package com.hotel.view;

import com.hotel.controller.InvoiceController;
import com.hotel.controller.PaymentController;
import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceDetail;
import com.hotel.entity.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class InvoiceDetailDialog {

    private InvoiceDetailDialog() {
    }

    public static void show(
            Component parent,
            InvoiceController invoiceController,
            PaymentController paymentController,
            Long invoiceId,
            DateTimeFormatter dateTimeFormatter,
            String title
    ) {
        if (invoiceId == null) {
            return;
        }

        Invoice invoice = invoiceController.getInvoiceById(invoiceId);
        if (invoice == null) {
            UiMessages.showError(parent, "Invoice not found.");
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

        DefaultTableModel detailModel = ViewSupport.createReadOnlyTableModel("Description", "Quantity", "Unit Price", "Amount");
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

        JOptionPane.showMessageDialog(parent, dialogPanel, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private static String formatInvoiceStatus(String status) {
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
