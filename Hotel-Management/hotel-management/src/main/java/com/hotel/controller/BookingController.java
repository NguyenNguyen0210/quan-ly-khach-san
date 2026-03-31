package com.hotel.controller;

import com.hotel.entity.Booking;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;
import com.hotel.service.base.BookingService;
import com.hotel.service.base.BookingServiceImpl;
import com.hotel.service.base.CustomerService;
import com.hotel.service.base.CustomerServiceImpl;
import com.hotel.service.base.RoomService;
import com.hotel.service.base.RoomServiceImpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BookingController {

    private final BookingService bookingService = new BookingServiceImpl();
    private final RoomService roomService = new RoomServiceImpl();
    private final CustomerService customerService = new CustomerServiceImpl();

    public void placeBooking(Long customerId, Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        Objects.requireNonNull(customerId, "Customer id is required");
        Objects.requireNonNull(roomId, "Room id is required");
        Objects.requireNonNull(checkInDate, "Check-in date is required");
        Objects.requireNonNull(checkOutDate, "Check-out date is required");

        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found for id " + customerId);
        }

        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found for id " + roomId);
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);

        bookingService.placeBooking(booking);
    }

    public Booking getBookingById(Long bookingId) {
        if (bookingId == null) {
            return null;
        }
        return bookingService.getBookingById(bookingId);
    }

    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        if (customerId == null) {
            return new ArrayList<>();
        }
        return bookingService.getBookingsByCustomer(customerId);
    }

    public List<Booking> getActiveBookings() {
        return bookingService.getActiveBookings();
    }

    public void updateBooking(Booking booking) {
        bookingService.updateBooking(booking);
    }

    public void checkIn(Long bookingId) {
        bookingService.checkIn(bookingId);
    }

    public void checkOut(Long bookingId) {
        bookingService.checkOut(bookingId);
    }

    public void checkOutAndCloseInvoice(Long bookingId, Long invoiceId) {
        Objects.requireNonNull(bookingId, "Booking id is required");
        Objects.requireNonNull(invoiceId, "Invoice id is required");
        bookingService.checkOutAndCloseInvoice(bookingId, invoiceId);
    }

    public void cancelBooking(Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        Objects.requireNonNull(roomId, "Room id is required");
        Objects.requireNonNull(checkIn, "Check-in date is required");
        Objects.requireNonNull(checkOut, "Check-out date is required");
        return bookingService.isRoomAvailable(roomId, toDate(checkIn), toDate(checkOut));
    }

    public boolean hasActiveBooking(Long roomId) {
        return bookingService.hasActiveBooking(roomId);
    }

    public Booking getActiveBookingForRoom(Long roomId) {
        if (roomId == null) {
            return null;
        }
        return bookingService.getActiveBookingForRoom(roomId);
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        Objects.requireNonNull(checkIn, "Check-in date is required");
        Objects.requireNonNull(checkOut, "Check-out date is required");

        List<Room> availableRooms = new ArrayList<>();
        for (Room room : roomService.getAllRooms()) {
            if (isRoomAvailable(room.getId(), checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
