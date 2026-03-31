package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.ServiceUsage;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class ServiceUsageDAO extends GenericDAOImpl<ServiceUsage, Long> {

    public ServiceUsageDAO() {
        super(ServiceUsage.class);
    }

    public List<ServiceUsage> findByBookingId(Long bookingId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select distinct su from ServiceUsage su " +
                                    "join fetch su.service " +
                                    "join fetch su.booking " +
                                    "left join fetch su.addedBy " +
                                    "where su.booking.id = :bookingId",
                            ServiceUsage.class)
                    .setParameter("bookingId", bookingId)
                    .getResultList();
        }
    }

    public List<ServiceUsage> findByServiceId(Long serviceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from ServiceUsage where service.id = :serviceId", ServiceUsage.class)
                    .setParameter("serviceId", serviceId)
                    .getResultList();
        }
    }
}
