package com.hotel.service.base;

import com.hotel.entity.Payment;
import java.util.List;

public interface PaymentService {

    void processPayment(Long invoiceId, Double amount, String paymentMethod);

    Payment getPaymentById(Long id);

    List<Payment> getPaymentsByInvoice(Long invoiceId);

    Payment getLatestPaymentByInvoice(Long invoiceId);

    Double getPaidAmount(Long invoiceId);

    Double getOutstandingAmount(Long invoiceId);
}
