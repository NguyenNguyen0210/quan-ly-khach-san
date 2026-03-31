package com.hotel.service.base;

import com.hotel.entity.InvoiceDetail;
import com.hotel.entity.Invoice;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {

    Invoice createInvoiceForBooking(Long bookingId);

    Invoice getInvoiceById(Long id);

    List<Invoice> getAllInvoices();

    List<Invoice> getInvoicesByBookingId(Long bookingId);

    List<InvoiceDetail> getInvoiceDetails(Long invoiceId);

    List<Invoice> getInvoicesByCreatedDateRange(LocalDateTime from, LocalDateTime to);

    Double calculateInvoiceAmount(Long bookingId);

    void updateInvoice(Invoice invoice);

    void closeInvoice(Long invoiceId);
}
