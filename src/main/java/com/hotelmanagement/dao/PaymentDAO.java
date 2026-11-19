package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Payment;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Session;

public class PaymentDAO extends BaseDAO<Payment> {

    public PaymentDAO() {
        super(Payment.class, "paymentId", true);
    }

    public List<Payment> findAllNewestFirst(Session session) {
        return session.createQuery("from Payment p order by p.paymentId desc", Payment.class).getResultList();
    }

    public List<Payment> findByBooking(Session session, Integer bookingId) {
        return session.createQuery("from Payment p where p.booking.bookingId = :b order by p.paymentId", Payment.class)
                .setParameter("b", bookingId).getResultList();
    }

    public List<Payment> findByCustomer(Session session, Integer customerId) {
        return session.createQuery(
                "from Payment p where p.booking.customer.customerId = :c order by p.paymentId desc", Payment.class)
                .setParameter("c", customerId).getResultList();
    }

    public BigDecimal sumAll(Session session) {
        BigDecimal v = session.createQuery("select sum(p.amount) from Payment p", BigDecimal.class).getSingleResult();
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal sumByBooking(Session session, Integer bookingId) {
        BigDecimal v = session.createQuery("select sum(p.amount) from Payment p where p.booking.bookingId = :b", BigDecimal.class)
                .setParameter("b", bookingId).getSingleResult();
        return v == null ? BigDecimal.ZERO : v;
    }
}
