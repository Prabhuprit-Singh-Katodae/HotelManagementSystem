package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `payment`. */
@Entity
@Table(name = "payment")
public class Payment implements Identifiable {

    @Id
    @Column(name = "payment_id")
    private Integer paymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    public Payment() { }

    @Override public Integer getId() { return paymentId; }
    @Override public void setId(Integer id) { this.paymentId = id; }

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer v) { this.paymentId = v; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking v) { this.booking = v; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { this.amount = v; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate v) { this.paymentDate = v; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String v) { this.paymentMethod = v; }


}
