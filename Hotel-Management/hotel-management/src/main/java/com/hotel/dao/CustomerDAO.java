package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.Customer;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class CustomerDAO extends GenericDAOImpl<Customer, Long> {

    public CustomerDAO() {
        super(Customer.class);
    }

    public Customer findByPhone(String phone) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Customer> results = session.createQuery("from Customer where phone = :phone", Customer.class)
                    .setParameter("phone", phone)
                    .getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }

    public Customer findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Customer> results = session.createQuery("from Customer where email = :email", Customer.class)
                    .setParameter("email", email)
                    .getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }

    public Customer findByIdCard(String idCard) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Customer> results = session.createQuery("from Customer where idCard = :idCard", Customer.class)
                    .setParameter("idCard", idCard)
                    .getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }
}
