package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `booking`. */
@Entity
@Table(name = "booking")
public class Booking implements Identifiable {

    @Id
    @Column(name = "booking_id")
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "status", length = 30)
    private String status;

    @OneToMany(mappedBy = "booking")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    private List<ServiceBooking> serviceBookings = new ArrayList<>();

    public Booking() { }

    @Override public Integer getId() { return bookingId; }
    @Override public void setId(Integer id) { this.bookingId = id; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer v) { this.bookingId = v; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer v) { this.customer = v; }

    public Room getRoom() { return room; }
    public void setRoom(Room v) { this.room = v; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate v) { this.checkIn = v; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate v) { this.checkOut = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> v) { this.payments = v; }

    public List<ServiceBooking> getServiceBookings() { return serviceBookings; }
    public void setServiceBookings(List<ServiceBooking> v) { this.serviceBookings = v; }


    public static final String PENDING = "Pending";
    public static final String CONFIRMED = "Confirmed";
    public static final String CHECKED_IN = "Checked-In";
    public static final String CHECKED_OUT = "Checked-Out";
    public static final String CANCELLED = "Cancelled";

    /** Number of nights (at least 1). Not stored in the database. */
    public long getNights() {
        if (checkIn == null || checkOut == null) return 1;
        long n = ChronoUnit.DAYS.between(checkIn, checkOut);
        return n < 1 ? 1 : n;
    }

}
