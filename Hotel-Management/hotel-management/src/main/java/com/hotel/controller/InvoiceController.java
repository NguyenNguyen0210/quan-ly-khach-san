package com.hotel.controller;

import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceDetail;
import com.hotel.service.base.InvoiceService;
import com.hotel.service.base.InvoiceServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class InvoiceController {

    private final InvoiceService invoiceService = new InvoiceServiceImpl();

    public Invoice createInvoiceForBooking(Long bookingId) {
        Objects.requireNonNull(bookingId, "Booking id is required");
        return invoiceService.createInvoiceForBooking(bookingId);
    }

    public Invoice getInvoiceById(Long invoiceId) {
        if (invoiceId == null) {
            return null;
        }
        return invoiceService.getInvoiceById(invoiceId);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    public List<Invoice> getInvoicesByBooking(Long bookingId) {
        if (bookingId == null) {
            return List.of();
        }
        return invoiceService.getInvoicesByBookingId(bookingId);
    }

    public List<InvoiceDetail> getInvoiceDetails(Long invoiceId) {
        if (invoiceId == null) {
            return List.of();
        }
        return invoiceService.getInvoiceDetails(invoiceId);
    }

    public List<Invoice> getInvoicesByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return List.of();
        }
        return invoiceService.getInvoicesByCreatedDateRange(from, to);
    }

    public Double calculateInvoiceAmount(Long bookingId) {
        if (bookingId == null) {
            return 0.0;
        }
        return invoiceService.calculateInvoiceAmount(bookingId);
    }

    public void updateInvoice(Invoice invoice) {
        invoiceService.updateInvoice(invoice);
    }

    public void closeInvoice(Long invoiceId) {
        if (invoiceId == null) {
            return;
        }
        invoiceService.closeInvoice(invoiceId);
    }
}
