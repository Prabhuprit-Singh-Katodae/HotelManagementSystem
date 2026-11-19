package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `service_booking`. */
@Entity
@Table(name = "service_booking")
public class ServiceBooking implements Identifiable {

    @Id
    @Column(name = "service_booking_id")
    private Integer serviceBookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private HotelService service;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    public ServiceBooking() { }

    @Override public Integer getId() { return serviceBookingId; }
    @Override public void setId(Integer id) { this.serviceBookingId = id; }

    public Integer getServiceBookingId() { return serviceBookingId; }
    public void setServiceBookingId(Integer v) { this.serviceBookingId = v; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking v) { this.booking = v; }

    public HotelService getService() { return service; }
    public void setService(HotelService v) { this.service = v; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { this.quantity = v; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate v) { this.serviceDate = v; }


    /** quantity x service price. Not stored in the database. */
    public BigDecimal getLineTotal() {
        if (service == null || service.getPrice() == null || quantity == null) return BigDecimal.ZERO;
        return service.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

}
