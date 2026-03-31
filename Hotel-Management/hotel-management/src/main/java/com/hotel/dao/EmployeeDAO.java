package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.Employee;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class EmployeeDAO extends GenericDAOImpl<Employee, Long> {

    public EmployeeDAO() {
        super(Employee.class);
    }

    public List<Employee> findByRole(String role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Employee where role = :role", Employee.class)
                    .setParameter("role", role)
                    .getResultList();
        }
    }

    public Employee findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Employee where username = :username", Employee.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }
}
