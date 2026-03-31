package com.hotel.service.base;

import com.hotel.entity.*;
import java.util.*;
public interface BookingService {

    // Create
    void placeBooking(Booking booking);

    // Read
    Booking getBookingById(Long id);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByCustomer(Long customerId);
    List<Booking> getActiveBookings();
    boolean hasActiveBooking(Long roomId);
    Booking getActiveBookingForRoom(Long roomId);

    // Update
    void updateBooking(Booking booking);

    // Business flow
    void checkIn(Long bookingId);
    void checkOut(Long bookingId);
    void checkOutAndCloseInvoice(Long bookingId, Long invoiceId);
    void cancelBooking(Long bookingId);

    // Validation
    boolean isRoomAvailable(Long roomId, Date checkIn, Date checkOut);
}
