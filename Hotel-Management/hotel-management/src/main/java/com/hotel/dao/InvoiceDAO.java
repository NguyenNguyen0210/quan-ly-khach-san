package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.Invoice;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDAO extends GenericDAOImpl<Invoice, Long> {

    public InvoiceDAO() {
        super(Invoice.class);
    }

    public List<Invoice> findByBookingId(Long bookingId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select i from Invoice i " +
                                    "join fetch i.booking b " +
                                    "join fetch b.customer " +
                                    "join fetch b.room r " +
                                    "join fetch r.roomType " +
                                    "where b.id = :bookingId",
                            Invoice.class)
                    .setParameter("bookingId", bookingId)
                    .getResultList();
        }
    }

    public List<Invoice> findByCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select i from Invoice i " +
                                    "join fetch i.booking b " +
                                    "join fetch b.customer " +
                                    "join fetch b.room r " +
                                    "join fetch r.roomType " +
                                    "where i.createdDate between :fromDate and :toDate",
                            Invoice.class)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
        }
    }

    public List<Invoice> findAllWithBookingGraph() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select i from Invoice i " +
                                    "join fetch i.booking b " +
                                    "join fetch b.customer " +
                                    "join fetch b.room r " +
                                    "join fetch r.roomType " +
                                    "order by i.createdDate desc",
                            Invoice.class)
                    .getResultList();
        }
    }
}
