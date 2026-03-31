package com.hotel.util;

import com.hotel.entity.Booking;
import com.hotel.entity.Customer;
import com.hotel.entity.Employee;
import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceDetail;
import com.hotel.entity.Payment;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;
import com.hotel.entity.Service;
import com.hotel.entity.ServiceUsage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            configuration.addAnnotatedClass(Booking.class);
            configuration.addAnnotatedClass(Customer.class);
            configuration.addAnnotatedClass(Employee.class);
            configuration.addAnnotatedClass(Invoice.class);
            configuration.addAnnotatedClass(InvoiceDetail.class);
            configuration.addAnnotatedClass(Payment.class);
            configuration.addAnnotatedClass(Room.class);
            configuration.addAnnotatedClass(RoomType.class);
            configuration.addAnnotatedClass(Service.class);
            configuration.addAnnotatedClass(ServiceUsage.class);

            return configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SessionFactory", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}