package com.hotel.controller;

import com.hotel.entity.Payment;
import com.hotel.service.base.PaymentService;
import com.hotel.service.base.PaymentServiceImpl;

import java.util.List;
import java.util.Objects;

public class PaymentController {

    private final PaymentService paymentService = new PaymentServiceImpl();

    public void processPayment(Long invoiceId, Double amount, String paymentMethod) {
        paymentService.processPayment(invoiceId, amount, paymentMethod);
    }

    public Payment getPaymentById(Long paymentId) {
        if (paymentId == null) {
            return null;
        }
        return paymentService.getPaymentById(paymentId);
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        if (invoiceId == null) {
            return List.of();
        }
        return paymentService.getPaymentsByInvoice(invoiceId);
    }

    public Payment getLatestPaymentByInvoice(Long invoiceId) {
        if (invoiceId == null) {
            return null;
        }
        return paymentService.getLatestPaymentByInvoice(invoiceId);
    }

    public Double getPaidAmount(Long invoiceId) {
        if (invoiceId == null) {
            return 0.0;
        }
        return paymentService.getPaidAmount(invoiceId);
    }

    public Double getOutstandingAmount(Long invoiceId) {
        if (invoiceId == null) {
            return 0.0;
        }
        return paymentService.getOutstandingAmount(invoiceId);
    }
}
