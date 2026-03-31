package com.hotel.service.base;

import com.hotel.dao.InvoiceDAO;
import com.hotel.dao.PaymentDAO;
import com.hotel.entity.Employee;
import com.hotel.entity.Invoice;
import com.hotel.entity.Payment;
import com.hotel.security.AuthSession;
import com.hotel.security.EmployeeRoles;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    public void processPayment(Long invoiceId, Double amount, String paymentMethod) {
        AuthSession.requireRole("process payments", EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
        Objects.requireNonNull(invoiceId, "Invoice id is required");
        Objects.requireNonNull(amount, "Payment amount is required");
        Objects.requireNonNull(paymentMethod, "Payment method is required");

        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive.");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Invoice invoice = session.get(Invoice.class, invoiceId);
            Employee currentEmployee = AuthSession.getCurrentEmployee() == null ? null : session.get(Employee.class, AuthSession.getCurrentEmployee().getId());
            if (invoice == null) {
                throw new IllegalArgumentException("Invoice not found for id " + invoiceId);
            }
            if (invoice.getTotalAmount() == null) {
                throw new IllegalStateException("Invoice total amount is not initialized.");
            }
            if (Invoice.STATUS_CLOSED.equalsIgnoreCase(invoice.getStatus())) {
                throw new IllegalStateException("This invoice has already been closed.");
            }

            Double paid = session.createQuery(
                            "select coalesce(sum(p.amount), 0) from Payment p where p.invoice.id = :invoiceId",
                            Double.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResult();
            double outstanding = invoice.getTotalAmount() - (paid == null ? 0.0 : paid);
            if (amount > outstanding) {
                throw new IllegalStateException("Payment amount exceeds outstanding balance.");
            }

            Payment payment = new Payment();
            payment.setInvoice(invoice);
            payment.setAmount(amount);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setProcessedBy(currentEmployee);
            session.persist(payment);

            double remainingAfterPayment = outstanding - amount;
            if (remainingAfterPayment <= 0.000001d) {
                invoice.setStatus(Invoice.STATUS_PAID);
                invoice.setSettledDate(LocalDateTime.now());
            } else {
                invoice.setStatus(Invoice.STATUS_PARTIALLY_PAID);
                invoice.setSettledDate(null);
            }
            session.merge(invoice);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Payment getPaymentById(Long id) {
        if (id == null) {
            return null;
        }
        return paymentDAO.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        if (invoiceId == null) {
            return List.of();
        }
        return paymentDAO.findByInvoiceId(invoiceId);
    }

    @Override
    public Payment getLatestPaymentByInvoice(Long invoiceId) {
        List<Payment> payments = getPaymentsByInvoice(invoiceId);
        return payments.isEmpty() ? null : payments.get(0);
    }

    @Override
    public Double getPaidAmount(Long invoiceId) {
        List<Payment> payments = getPaymentsByInvoice(invoiceId);
        double total = 0.0;
        for (Payment payment : payments) {
            if (payment.getAmount() != null) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    @Override
    public Double getOutstandingAmount(Long invoiceId) {
        Invoice invoice = invoiceDAO.findById(invoiceId);
        if (invoice == null || invoice.getTotalAmount() == null) {
            return 0.0;
        }
        return Math.max(0.0, invoice.getTotalAmount() - getPaidAmount(invoiceId));
    }
}
