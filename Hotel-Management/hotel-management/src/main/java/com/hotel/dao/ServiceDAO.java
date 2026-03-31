package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.Service;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class ServiceDAO extends GenericDAOImpl<Service, Long> {

    public ServiceDAO() {
        super(Service.class);
    }

    public Service findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Service> results = session.createQuery("from Service where name = :name", Service.class)
                    .setParameter("name", name)
                    .getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }
}
