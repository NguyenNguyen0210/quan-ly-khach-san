package com.hotel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
public class Invoice extends BaseEntity {
    public static final String STATUS_ISSUED = "ISSUED";
    public static final String STATUS_PARTIALLY_PAID = "PARTIALLY_PAID";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_CLOSED = "CLOSED";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "settled_date")
    private LocalDateTime settledDate;

    public Invoice() {
    }

    @PrePersist
    protected void onPersist() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = STATUS_ISSUED;
        }
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(LocalDateTime settledDate) {
        this.settledDate = settledDate;
    }
}
