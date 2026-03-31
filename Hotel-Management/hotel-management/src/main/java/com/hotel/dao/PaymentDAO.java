package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.Payment;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentDAO extends GenericDAOImpl<Payment, Long> {

    public PaymentDAO() {
        super(Payment.class);
    }

    public List<Payment> findByInvoiceId(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select p from Payment p left join fetch p.processedBy where p.invoice.id = :invoiceId order by p.paymentDate desc",
                            Payment.class)
                    .setParameter("invoiceId", invoiceId)
                    .getResultList();
        }
    }

    public List<Payment> findByPaymentDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Payment where paymentDate between :fromDate and :toDate", Payment.class)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
        }
    }
}
