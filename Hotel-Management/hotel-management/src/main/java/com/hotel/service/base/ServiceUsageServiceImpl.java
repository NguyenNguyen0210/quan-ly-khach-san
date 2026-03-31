package com.hotel.service.base;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.ServiceDAO;
import com.hotel.dao.ServiceUsageDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Employee;
import com.hotel.entity.Invoice;
import com.hotel.entity.Service;
import com.hotel.entity.ServiceUsage;
import com.hotel.security.AuthSession;
import com.hotel.security.EmployeeRoles;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Objects;

public class ServiceUsageServiceImpl implements ServiceUsageService {

    private final ServiceUsageDAO serviceUsageDAO = new ServiceUsageDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    public void addServiceUsage(Long bookingId, Long serviceId, int quantity) {
        AuthSession.requireRole("add services", EmployeeRoles.SERVICE_STAFF, EmployeeRoles.MANAGER);
        Objects.requireNonNull(bookingId, "Booking id is required");
        Objects.requireNonNull(serviceId, "Service id is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Booking booking = session.get(Booking.class, bookingId);
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for id " + bookingId);
            }
            if (booking.getStatus() == null || booking.getStatus().equals("CANCELLED") || booking.getStatus().equals("CHECKED_OUT")) {
                throw new IllegalStateException("Cannot add service to this booking.");
            }
            if (!"CHECKED_IN".equalsIgnoreCase(booking.getStatus())) {
                throw new IllegalStateException("Services can only be added after the guest has checked in.");
            }

            Invoice invoice = session.createQuery(
                            "select i from Invoice i where i.booking.id = :bookingId",
                            Invoice.class)
                    .setParameter("bookingId", bookingId)
                    .setMaxResults(1)
                    .uniqueResult();
            if (invoice != null) {
                if (!Invoice.STATUS_ISSUED.equalsIgnoreCase(invoice.getStatus())) {
                    throw new IllegalStateException("Invoice is already locked. You cannot add more services.");
                }
                Long paymentCount = session.createQuery(
                                "select count(p.id) from Payment p where p.invoice.id = :invoiceId",
                                Long.class)
                        .setParameter("invoiceId", invoice.getId())
                        .uniqueResult();
                if (paymentCount != null && paymentCount > 0) {
                    throw new IllegalStateException("Invoice already has payment records. Additional services are not allowed.");
                }
            }

            Service service = session.get(Service.class, serviceId);
            Employee currentEmployee = AuthSession.getCurrentEmployee() == null ? null : session.get(Employee.class, AuthSession.getCurrentEmployee().getId());
            if (service == null) {
                throw new IllegalArgumentException("Service not found for id " + serviceId);
            }

            Integer available = service.getQuantity();
            if (available == null) {
                available = 0;
            }
            if (available < quantity) {
                throw new IllegalStateException("Not enough service quantity available.");
            }

            ServiceUsage usage = new ServiceUsage();
            usage.setBooking(booking);
            usage.setService(service);
            usage.setQuantity(quantity);
            usage.setAddedBy(currentEmployee);
            session.persist(usage);

            service.setQuantity(available - quantity);
            session.merge(service);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ignored) {
                    // Preserve the original business error instead of masking it
                    // with a secondary rollback/connection exception.
                }
            }
            throw e;
        }
    }

    @Override
    public List<ServiceUsage> getServiceUsageByBooking(Long bookingId) {
        if (bookingId == null) {
            return List.of();
        }
        return serviceUsageDAO.findByBookingId(bookingId);
    }

    @Override
    public Double calculateServiceUsageTotal(Long bookingId) {
        List<ServiceUsage> usages = getServiceUsageByBooking(bookingId);
        double total = 0.0;
        for (ServiceUsage usage : usages) {
            if (usage.getService() != null && usage.getQuantity() != null) {
                total += usage.getService().getPrice() * usage.getQuantity();
            }
        }
        return total;
    }

    @Override
    public void removeServiceUsage(Long serviceUsageId) {
        if (serviceUsageId == null) {
            throw new IllegalArgumentException("Service usage id is required.");
        }
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            ServiceUsage usage = session.createQuery(
                            "select su from ServiceUsage su join fetch su.service where su.id = :id",
                            ServiceUsage.class)
                    .setParameter("id", serviceUsageId)
                    .uniqueResult();
            if (usage == null) {
                tx.commit();
                return;
            }
            Service service = usage.getService();
            if (service != null && usage.getQuantity() != null) {
                Integer available = service.getQuantity();
                if (available == null) {
                    available = 0;
                }
                service.setQuantity(available + usage.getQuantity());
                session.merge(service);
            }
            session.remove(usage);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ignored) {
                    // Preserve the original business error instead of masking it
                    // with a secondary rollback/connection exception.
                }
            }
            throw e;
        }
    }
}
