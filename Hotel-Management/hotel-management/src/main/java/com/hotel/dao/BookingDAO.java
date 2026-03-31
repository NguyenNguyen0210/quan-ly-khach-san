package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.Booking;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;

public class BookingDAO extends GenericDAOImpl<Booking, Long> {

    public BookingDAO() {
        super(Booking.class);
    }

    public List<Booking> findByCustomerId(Long customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Booking where customer.id = :customerId", Booking.class)
                    .setParameter("customerId", customerId)
                    .getResultList();
        }
    }

    public List<Booking> findByRoomId(Long roomId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select b from Booking b " +
                                    "join fetch b.room r " +
                                    "left join fetch r.roomType " +
                                    "join fetch b.customer " +
                                    "where b.room.id = :roomId",
                            Booking.class)
                    .setParameter("roomId", roomId)
                    .getResultList();
        }
    }

    public List<Booking> findByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Booking where status = :status", Booking.class)
                    .setParameter("status", status)
                    .getResultList();
        }
    }

    public List<Booking> findAllWithRoomAndCustomer() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select b from Booking b join fetch b.room join fetch b.customer", Booking.class)
                    .getResultList();
        }
    }

    public Booking findByIdWithRoomAndCustomer(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select b from Booking b join fetch b.room join fetch b.customer where b.id = :id", Booking.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public Booking findByIdWithRoomCustomerAndRoomType(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select b from Booking b " +
                                    "join fetch b.room r " +
                                    "join fetch r.roomType " +
                                    "join fetch b.customer " +
                                    "where b.id = :id",
                            Booking.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public List<Booking> findByCheckInBetween(LocalDate fromDate, LocalDate toDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Booking where checkInDate between :fromDate and :toDate", Booking.class)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
        }
    }
}
