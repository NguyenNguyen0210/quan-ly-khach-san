package com.hotel.service.base;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.InvoiceDAO;
import com.hotel.dao.InvoiceDetailDAO;
import com.hotel.dao.ServiceUsageDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceDetail;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;
import com.hotel.entity.ServiceUsage;
import com.hotel.security.AuthSession;
import com.hotel.security.EmployeeRoles;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO invoiceDetailDAO = new InvoiceDetailDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceUsageDAO serviceUsageDAO = new ServiceUsageDAO();

    @Override
    public Invoice createInvoiceForBooking(Long bookingId) {
        AuthSession.requireRole("create invoices", EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
        Objects.requireNonNull(bookingId, "Booking id is required");

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Booking booking = loadBookingWithGraph(session, bookingId);
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for id " + bookingId);
            }
            if (!"CHECKED_IN".equalsIgnoreCase(booking.getStatus())) {
                throw new IllegalStateException("Invoice can only be created for a checked-in booking.");
            }

            Invoice existingInvoice = session.createQuery(
                            "select i from Invoice i where i.booking.id = :bookingId",
                            Invoice.class)
                    .setParameter("bookingId", bookingId)
                    .setMaxResults(1)
                    .uniqueResult();

            if (existingInvoice != null) {
                if (isInvoiceLocked(session, existingInvoice)) {
                    tx.commit();
                    return existingInvoice;
                }
                syncInvoice(session, existingInvoice, booking);
                tx.commit();
                return existingInvoice;
            }

            double totalAmount = calculateInvoiceAmount(booking);

            Invoice invoice = new Invoice();
            invoice.setBooking(booking);
            invoice.setTotalAmount(totalAmount);
            invoice.setStatus(Invoice.STATUS_ISSUED);
            session.persist(invoice);
            createInvoiceDetails(session, invoice, booking);
            tx.commit();
            return invoice;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        if (id == null) {
            return null;
        }
        return invoiceDAO.findById(id);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.findAllWithBookingGraph();
    }

    @Override
    public List<Invoice> getInvoicesByBookingId(Long bookingId) {
        if (bookingId == null) {
            return List.of();
        }
        return invoiceDAO.findByBookingId(bookingId);
    }

    @Override
    public List<InvoiceDetail> getInvoiceDetails(Long invoiceId) {
        if (invoiceId == null) {
            return List.of();
        }
        return invoiceDetailDAO.findByInvoiceId(invoiceId);
    }

    @Override
    public List<Invoice> getInvoicesByCreatedDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return List.of();
        }
        return invoiceDAO.findByCreatedDateBetween(from, to);
    }

    @Override
    public Double calculateInvoiceAmount(Long bookingId) {
        Objects.requireNonNull(bookingId, "Booking id is required");

        Booking booking = bookingDAO.findByIdWithRoomCustomerAndRoomType(bookingId);
        if (booking == null) {
            return 0.0;
        }

        return calculateInvoiceAmount(booking);
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        Objects.requireNonNull(invoice, "Invoice must not be null");
        if (invoice.getId() == null) {
            throw new IllegalArgumentException("Invoice id is required for update.");
        }
        invoiceDAO.update(invoice);
    }

    @Override
    public void closeInvoice(Long invoiceId) {
        AuthSession.requireRole("close invoices", EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
        Objects.requireNonNull(invoiceId, "Invoice id is required");

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice == null) {
                throw new IllegalArgumentException("Invoice not found for id " + invoiceId);
            }

            Double paid = session.createQuery(
                            "select coalesce(sum(p.amount), 0) from Payment p where p.invoice.id = :invoiceId",
                            Double.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResult();
            double totalAmount = invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount();
            double paidAmount = paid == null ? 0.0 : paid;
            if (paidAmount + 0.000001d < totalAmount) {
                throw new IllegalStateException("Invoice must be fully paid before it can be closed.");
            }

            invoice.setStatus(Invoice.STATUS_CLOSED);
            if (invoice.getSettledDate() == null) {
                invoice.setSettledDate(LocalDateTime.now());
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

    private double calculateRoomTotal(Booking booking) {
        if (booking.getRoom() == null || booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            return 0.0;
        }
        Room room = booking.getRoom();
        RoomType roomType = room.getRoomType();
        if (roomType == null || roomType.getPricePerNight() == null) {
            return 0.0;
        }

        long nights = Duration.between(booking.getCheckInDate().atStartOfDay(), booking.getCheckOutDate().atStartOfDay()).toDays();
        if (nights <= 0) {
            nights = 1;
        }
        return nights * roomType.getPricePerNight();
    }

    private double calculateServiceTotal(Long bookingId) {
        List<ServiceUsage> usages = serviceUsageDAO.findByBookingId(bookingId);
        return calculateServiceTotal(usages);
    }

    private double calculateServiceTotal(List<ServiceUsage> usages) {
        double total = 0.0;
        for (ServiceUsage usage : usages) {
            if (usage.getService() != null && usage.getQuantity() != null && usage.getService().getPrice() != null) {
                total += usage.getService().getPrice() * usage.getQuantity();
            }
        }
        return total;
    }

    private double calculateInvoiceAmount(Booking booking) {
        return calculateRoomTotal(booking) + calculateServiceTotal(loadServiceUsages(booking.getId()));
    }

    private void syncInvoice(Session session, Invoice invoice, Booking booking) {
        if (invoice == null || invoice.getId() == null || booking == null) {
            return;
        }

        double recalculatedTotal = calculateRoomTotal(booking) + calculateServiceTotal(loadServiceUsages(session, booking.getId()));
        invoice.setTotalAmount(recalculatedTotal);
        session.merge(invoice);

        List<InvoiceDetail> existingDetails = session.createQuery(
                        "select d from InvoiceDetail d where d.invoice.id = :invoiceId",
                        InvoiceDetail.class)
                .setParameter("invoiceId", invoice.getId())
                .getResultList();
        for (InvoiceDetail detail : existingDetails) {
            session.remove(detail);
        }

        createInvoiceDetails(session, invoice, booking);
    }

    private boolean isInvoiceLocked(Session session, Invoice invoice) {
        if (invoice == null || invoice.getId() == null) {
            return false;
        }
        if (Invoice.STATUS_PARTIALLY_PAID.equalsIgnoreCase(invoice.getStatus())
                || Invoice.STATUS_PAID.equalsIgnoreCase(invoice.getStatus())
                || Invoice.STATUS_CLOSED.equalsIgnoreCase(invoice.getStatus())) {
            return true;
        }
        Long paymentCount = session.createQuery(
                        "select count(p.id) from Payment p where p.invoice.id = :invoiceId",
                        Long.class)
                .setParameter("invoiceId", invoice.getId())
                .uniqueResult();
        return paymentCount != null && paymentCount > 0;
    }

    private void createInvoiceDetails(Session session, Invoice invoice, Booking booking) {
        InvoiceDetail roomCharge = buildRoomChargeDetail(invoice, booking);
        if (roomCharge != null) {
            session.persist(roomCharge);
        }

        List<ServiceUsage> usages = loadServiceUsages(session, booking.getId());
        for (ServiceUsage usage : usages) {
            InvoiceDetail serviceDetail = buildServiceDetail(invoice, usage);
            if (serviceDetail != null) {
                session.persist(serviceDetail);
            }
        }
    }

    private InvoiceDetail buildRoomChargeDetail(Invoice invoice, Booking booking) {
        if (booking == null || booking.getRoom() == null || booking.getRoom().getRoomType() == null) {
            return null;
        }
        Double nightlyRate = booking.getRoom().getRoomType().getPricePerNight();
        if (nightlyRate == null) {
            return null;
        }

        long nights = Duration.between(booking.getCheckInDate().atStartOfDay(), booking.getCheckOutDate().atStartOfDay()).toDays();
        if (nights <= 0) {
            nights = 1;
        }

        String roomNumber = booking.getRoom().getRoomNumber() == null ? "" : booking.getRoom().getRoomNumber();
        String roomTypeName = booking.getRoom().getRoomType().getName() == null ? "" : booking.getRoom().getRoomType().getName();

        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setDescription("Room " + roomNumber + " - " + roomTypeName);
        detail.setQuantity((int) nights);
        detail.setUnitPrice(nightlyRate);
        detail.calculateAmount();
        return detail;
    }

    private InvoiceDetail buildServiceDetail(Invoice invoice, ServiceUsage usage) {
        if (usage == null || usage.getService() == null || usage.getQuantity() == null || usage.getService().getPrice() == null) {
            return null;
        }

        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setService(usage.getService());
        detail.setDescription(usage.getService().getName());
        detail.setQuantity(usage.getQuantity());
        detail.setUnitPrice(usage.getService().getPrice());
        detail.calculateAmount();
        return detail;
    }

    private Booking loadBookingWithGraph(Session session, Long bookingId) {
        return session.createQuery(
                        "select b from Booking b " +
                                "join fetch b.room r " +
                                "join fetch r.roomType " +
                                "join fetch b.customer " +
                                "where b.id = :id",
                        Booking.class)
                .setParameter("id", bookingId)
                .uniqueResult();
    }

    private List<ServiceUsage> loadServiceUsages(Long bookingId) {
        return serviceUsageDAO.findByBookingId(bookingId);
    }

    private List<ServiceUsage> loadServiceUsages(Session session, Long bookingId) {
        if (bookingId == null) {
            return new ArrayList<>();
        }
        return session.createQuery(
                        "select su from ServiceUsage su join fetch su.service where su.booking.id = :bookingId",
                        ServiceUsage.class)
                .setParameter("bookingId", bookingId)
                .getResultList();
    }
}
