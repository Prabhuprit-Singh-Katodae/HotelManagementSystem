package com.hotelmanagement.service;

import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.entity.Payment;
import com.hotelmanagement.entity.ServiceBooking;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Everything needed to show a bill or an invoice for one booking. */
public class BillSummary {
    private Booking booking;
    private long nights;
    private BigDecimal roomRate = BigDecimal.ZERO;
    private BigDecimal roomCharges = BigDecimal.ZERO;
    private BigDecimal serviceCharges = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal paid = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
    private String paymentStatus = "Unpaid";
    private String paymentMethods = "-";
    private List<Payment> payments = new ArrayList<>();
    private List<ServiceBooking> serviceLines = new ArrayList<>();

    public Booking getBooking() { return booking; }
    public void setBooking(Booking v) { this.booking = v; }
    public long getNights() { return nights; }
    public void setNights(long v) { this.nights = v; }
    public BigDecimal getRoomRate() { return roomRate; }
    public void setRoomRate(BigDecimal v) { this.roomRate = v; }
    public BigDecimal getRoomCharges() { return roomCharges; }
    public void setRoomCharges(BigDecimal v) { this.roomCharges = v; }
    public BigDecimal getServiceCharges() { return serviceCharges; }
    public void setServiceCharges(BigDecimal v) { this.serviceCharges = v; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal v) { this.total = v; }
    public BigDecimal getPaid() { return paid; }
    public void setPaid(BigDecimal v) { this.paid = v; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal v) { this.balance = v; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String v) { this.paymentStatus = v; }
    public String getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(String v) { this.paymentMethods = v; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> v) { this.payments = v; }
    public List<ServiceBooking> getServiceLines() { return serviceLines; }
    public void setServiceLines(List<ServiceBooking> v) { this.serviceLines = v; }
}
