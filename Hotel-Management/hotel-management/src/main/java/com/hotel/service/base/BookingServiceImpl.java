package com.hotel.service.base;

import com.hotel.dao.BookingDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Customer;
import com.hotel.entity.Employee;
import com.hotel.entity.Invoice;
import com.hotel.entity.Room;
import com.hotel.security.AuthSession;
import com.hotel.security.EmployeeRoles;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BookingServiceImpl implements BookingService {

    private static final String STATUS_BOOKED = "BOOKED";
    private static final String STATUS_CHECKED_IN = "CHECKED_IN";
    private static final String STATUS_CHECKED_OUT = "CHECKED_OUT";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String ROOM_STATUS_AVAILABLE = "AVAILABLE";
    private static final String ROOM_STATUS_OCCUPIED = "OCCUPIED";

    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    public void placeBooking(Booking booking) {
        AuthSession.requireRole("create bookings", EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
        Objects.requireNonNull(booking, "Booking must not be null");
        Objects.requireNonNull(booking.getCustomer(), "Booking customer must not be null");
        Objects.requireNonNull(booking.getRoom(), "Booking room must not be null");
        Objects.requireNonNull(booking.getCheckInDate(), "Check-in date is required");
        Objects.requireNonNull(booking.getCheckOutDate(), "Check-out date is required");

        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date must not be after check-out date.");
        }

        if (!isRoomAvailable(booking.getRoom().getId(), toDate(booking.getCheckInDate()), toDate(booking.getCheckOutDate()))) {
            throw new IllegalStateException("Room is not available for the selected dates.");
        }

        if (booking.getStatus() == null || booking.getStatus().isBlank()) {
            booking.setStatus(STATUS_BOOKED);
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Customer customer = session.get(Customer.class, booking.getCustomer().getId());
            Room room = session.get(Room.class, booking.getRoom().getId());
            Employee currentEmployee = AuthSession.getCurrentEmployee() == null ? null : session.get(Employee.class, AuthSession.getCurrentEmployee().getId());
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found for id " + booking.getCustomer().getId());
            }
            if (room == null) {
                throw new IllegalArgumentException("Room not found for id " + booking.getRoom().getId());
            }

            if (!isRoomAvailable(session, room.getId(), booking.getCheckInDate(), booking.getCheckOutDate(), null)) {
                throw new IllegalStateException("Room is not available for the selected dates.");
            }

            booking.setCustomer(customer);
            booking.setRoom(room);
            booking.setCreatedBy(currentEmployee);
            session.persist(booking);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Booking getBookingById(Long id) {
        if (id == null) {
            return null;
        }
        Booking booking = bookingDAO.findByIdWithRoomAndCustomer(id);
        if (booking != null) {
            return booking;
        }
        return bookingDAO.findById(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        try {
            return bookingDAO.findAllWithRoomAndCustomer();
        } catch (Exception ex) {
            // fallback to non-fetch version if join fetch fails
            return bookingDAO.findAll();
        }
    }

    @Override
    public List<Booking> getBookingsByCustomer(Long customerId) {
        if (customerId == null) {
            return new ArrayList<>();
        }
        return bookingDAO.findByCustomerId(customerId);
    }

    @Override
    public List<Booking> getActiveBookings() {
        List<Booking> active = new ArrayList<>();
        active.addAll(bookingDAO.findByStatus(STATUS_BOOKED));
        active.addAll(bookingDAO.findByStatus(STATUS_CHECKED_IN));
        return active;
    }

    @Override
    public void updateBooking(Booking booking) {
        Objects.requireNonNull(booking, "Booking must not be null");
        if (booking.getId() == null) {
            throw new IllegalArgumentException("Booking id is required for update.");
        }
        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null && booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date must not be after check-out date.");
        }
        bookingDAO.update(booking);
    }

    @Override
    public boolean hasActiveBooking(Long roomId) {
        return getActiveBookingForRoom(roomId) != null;
    }

    @Override
    public Booking getActiveBookingForRoom(Long roomId) {
        if (roomId == null) {
            return null;
        }
        List<Booking> bookings = bookingDAO.findByRoomId(roomId);
        for (Booking booking : bookings) {
            if (isActiveBooking(booking)) {
                return booking;
            }
        }
        return null;
    }

    @Override
    public void checkIn(Long bookingId) {
        AuthSession.requireRole("check in guests", EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Booking booking = loadBookingWithRoom(session, bookingId);
            Employee currentEmployee = AuthSession.getCurrentEmployee() == null ? null : session.get(Employee.class, AuthSession.getCurrentEmployee().getId());
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for id " + bookingId);
            }
            if (STATUS_CANCELLED.equals(booking.getStatus())) {
                throw new IllegalStateException("Cannot check in a cancelled booking.");
            }
            if (STATUS_CHECKED_IN.equals(booking.getStatus())) {
                tx.commit();
                return;
            }
            Room room = booking.getRoom();
            if (room == null) {
                throw new IllegalStateException("Booking room is missing.");
            }
            booking.setStatus(STATUS_CHECKED_IN);
            booking.setCheckedInBy(currentEmployee);
            room.setStatus(ROOM_STATUS_OCCUPIED);
            session.merge(room);
            session.merge(booking);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public void checkOut(Long bookingId) {
        AuthSession.requireRole("check out guests", EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Booking booking = loadBookingWithRoom(session, bookingId);
            Employee currentEmployee = AuthSession.getCurrentEmployee() == null ? null : session.get(Employee.class, AuthSession.getCurrentEmployee().getId());
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for id " + bookingId);
            }
            if (STATUS_CANCELLED.equals(booking.getStatus())) {
                throw new IllegalStateException("Cannot check out a cancelled booking.");
            }
            if (STATUS_CHECKED_OUT.equals(booking.getStatus())) {
                tx.commit();
                return;
            }
            booking.setStatus(STATUS_CHECKED_OUT);
            booking.setCheckedOutBy(currentEmployee);
            Room room = booking.getRoom();
            if (room != null) {
                room.setStatus(ROOM_STATUS_AVAILABLE);
                session.merge(room);
            }
            session.merge(booking);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public void checkOutAndCloseInvoice(Long bookingId, Long invoiceId) {
        AuthSession.requireRole("check out guests", EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER);
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking id is required.");
        }
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice id is required.");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Booking booking = loadBookingWithRoom(session, bookingId);
            Employee currentEmployee = AuthSession.getCurrentEmployee() == null ? null : session.get(Employee.class, AuthSession.getCurrentEmployee().getId());
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for id " + bookingId);
            }
            if (STATUS_CANCELLED.equals(booking.getStatus())) {
                throw new IllegalStateException("Cannot check out a cancelled booking.");
            }

            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice == null) {
                throw new IllegalArgumentException("Invoice not found for id " + invoiceId);
            }
            if (invoice.getBooking() == null || !bookingId.equals(invoice.getBooking().getId())) {
                throw new IllegalStateException("Invoice does not belong to this booking.");
            }

            Double paid = session.createQuery(
                            "select coalesce(sum(p.amount), 0) from Payment p where p.invoice.id = :invoiceId",
                            Double.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResult();
            double totalAmount = invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount();
            double paidAmount = paid == null ? 0.0 : paid;
            if (paidAmount + 0.000001d < totalAmount) {
                throw new IllegalStateException("Invoice must be fully paid before checkout.");
            }

            if (!STATUS_CHECKED_OUT.equals(booking.getStatus())) {
                booking.setStatus(STATUS_CHECKED_OUT);
                booking.setCheckedOutBy(currentEmployee);
                Room room = booking.getRoom();
                if (room != null) {
                    room.setStatus(ROOM_STATUS_AVAILABLE);
                    session.merge(room);
                }
                session.merge(booking);
            }

            invoice.setStatus(Invoice.STATUS_CLOSED);
            if (invoice.getSettledDate() == null) {
                invoice.setSettledDate(java.time.LocalDateTime.now());
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

    @Override
    public void cancelBooking(Long bookingId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Booking booking = loadBookingWithRoom(session, bookingId);
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for id " + bookingId);
            }
            if (STATUS_CANCELLED.equals(booking.getStatus())) {
                tx.commit();
                return;
            }
            if (STATUS_CHECKED_IN.equals(booking.getStatus())) {
                throw new IllegalStateException("Cannot cancel a booking that has already checked in.");
            }
            booking.setStatus(STATUS_CANCELLED);
            session.merge(booking);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean isRoomAvailable(Long roomId, Date checkIn, Date checkOut) {
        Objects.requireNonNull(roomId, "Room id is required");
        Objects.requireNonNull(checkIn, "Check-in date is required");
        Objects.requireNonNull(checkOut, "Check-out date is required");

        LocalDate fromDate = toLocalDate(checkIn);
        LocalDate toDate = toLocalDate(checkOut);
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Check-in date must not be after check-out date.");
        }

        List<Booking> bookings = bookingDAO.findByRoomId(roomId);
        for (Booking booking : bookings) {
            if (!isActiveBooking(booking)) {
                continue;
            }
            LocalDate existingFrom = booking.getCheckInDate();
            LocalDate existingTo = booking.getCheckOutDate();
            if (existingFrom == null || existingTo == null) {
                continue;
            }
            boolean overlap = !existingTo.isBefore(fromDate) && !existingFrom.isAfter(toDate);
            if (overlap) {
                return false;
            }
        }
        return true;
    }

    private boolean isRoomAvailable(Session session, Long roomId, LocalDate fromDate, LocalDate toDate, Long excludeBookingId) {
        List<Booking> bookings = session.createQuery(
                        "select b from Booking b where b.room.id = :roomId and b.status in (:booked, :checkedIn)",
                        Booking.class)
                .setParameter("roomId", roomId)
                .setParameter("booked", STATUS_BOOKED)
                .setParameter("checkedIn", STATUS_CHECKED_IN)
                .getResultList();
        for (Booking booking : bookings) {
            if (excludeBookingId != null && excludeBookingId.equals(booking.getId())) {
                continue;
            }
            LocalDate existingFrom = booking.getCheckInDate();
            LocalDate existingTo = booking.getCheckOutDate();
            if (existingFrom == null || existingTo == null) {
                continue;
            }
            boolean overlap = !existingTo.isBefore(fromDate) && !existingFrom.isAfter(toDate);
            if (overlap) {
                return false;
            }
        }
        return true;
    }

    private boolean isActiveBooking(Booking booking) {
        if (booking == null || booking.getStatus() == null) {
            return false;
        }
        return STATUS_BOOKED.equals(booking.getStatus()) || STATUS_CHECKED_IN.equals(booking.getStatus());
    }

    private Booking loadBookingWithRoom(Session session, Long bookingId) {
        if (bookingId == null) {
            return null;
        }
        return session.createQuery("select b from Booking b join fetch b.room where b.id = :id", Booking.class)
                .setParameter("id", bookingId)
                .uniqueResult();
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
