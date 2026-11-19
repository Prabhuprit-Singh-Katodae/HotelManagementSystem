package com.hotelmanagement.service;

import com.hotelmanagement.dao.BookingDAO;
import com.hotelmanagement.dao.PaymentDAO;
import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.entity.Payment;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;

/**
 * Records payments. This is NOT a real payment gateway: staff record cash/UPI/card payments,
 * and customers can use a clearly labelled DEMO payment that just stores a payment record.
 */
public class PaymentService {

    public static final List<String> METHODS = List.of("Cash", "UPI", "Card");

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final BookingService bookingService = new BookingService();

    private static String normalizeMethod(String method) {
        for (String m : METHODS) {
            if (m.equalsIgnoreCase(method == null ? "" : method.trim())) return m;
        }
        throw new ServiceException("Choose a payment method: Cash, UPI or Card.");
    }

    public List<Payment> listAll() {
        return HibernateUtil.read(s -> paymentDAO.findAllNewestFirst(s));
    }

    public List<Payment> listForCustomer(Integer customerId) {
        return HibernateUtil.read(s -> paymentDAO.findByCustomer(s, customerId));
    }

    /** Staff records a payment of a chosen amount. */
    public Integer record(Integer bookingId, BigDecimal amount, String method, LocalDate date, LoginUser actor) {
        BigDecimal amt = Validator.money(amount, "Amount", false).setScale(2, RoundingMode.HALF_UP);
        String m = normalizeMethod(method);
        LocalDate when = date == null ? LocalDate.now() : date;
        return HibernateUtil.tx(s -> insert(s, bookingId, amt, m, when, actor));
    }

    /** Customer demo payment: pays the full outstanding balance of one of THEIR bookings. */
    public Integer demoPayFullBalance(Integer bookingId, String method, LoginUser actor) {
        String m = normalizeMethod(method);
        return HibernateUtil.tx(s -> {
            Booking b = bookingDAO.findById(s, bookingId);
            if (b == null) throw new ServiceException("Booking not found.");
            BigDecimal balance = bookingService.buildBill(s, b).getBalance();
            if (balance.signum() <= 0) throw new ServiceException("Nothing is due for this booking.");
            return insert(s, bookingId, balance.setScale(2, RoundingMode.HALF_UP), m, LocalDate.now(), actor);
        });
    }

    private Integer insert(Session s, Integer bookingId, BigDecimal amount, String method, LocalDate date, LoginUser actor) {
        Booking b = bookingDAO.findById(s, bookingId);
        if (b == null) throw new ServiceException("Booking not found.");
        if (actor.isCustomer() && (b.getCustomer() == null || !b.getCustomer().getCustomerId().equals(actor.getCustomerId()))) {
            throw new ServiceException("Booking not found.");
        }
        if (Booking.CANCELLED.equalsIgnoreCase(b.getStatus())) {
            throw new ServiceException("Payments cannot be recorded for a cancelled booking.");
        }
        BigDecimal balance = bookingService.buildBill(s, b).getBalance();
        if (amount.compareTo(balance) > 0) {
            throw new ServiceException("Amount is more than the balance due (" + balance.setScale(2, RoundingMode.HALF_UP) + ").");
        }
        Payment p = new Payment();
        p.setBooking(b);
        p.setAmount(amount);
        p.setPaymentDate(date);
        p.setPaymentMethod(method);
        paymentDAO.save(s, p);
        return p.getPaymentId();
    }

    public void delete(Integer paymentId) {
        HibernateUtil.txRun(s -> {
            Payment p = paymentDAO.findById(s, paymentId);
            if (p == null) throw new ServiceException("Payment not found.");
            paymentDAO.delete(s, p);
        });
    }
}
