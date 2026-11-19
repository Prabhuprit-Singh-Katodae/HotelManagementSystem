package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Booking;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;

public class BookingDAO extends BaseDAO<Booking> {

    public BookingDAO() {
        super(Booking.class, "bookingId", true);
    }

    /** All bookings, newest first, optionally filtered by status and a search text (customer name / room number / id). */
    public List<Booking> search(Session session, String status, String q) {
        StringBuilder hql = new StringBuilder("from Booking b where 1 = 1 ");
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasQ = q != null && !q.isBlank();
        if (hasStatus) hql.append("and b.status = :status ");
        if (hasQ) hql.append("and (lower(b.customer.name) like :q or b.room.roomNumber like :q or str(b.bookingId) = :id) ");
        hql.append("order by b.bookingId desc");
        var query = session.createQuery(hql.toString(), Booking.class);
        if (hasStatus) query.setParameter("status", status);
        if (hasQ) {
            query.setParameter("q", "%" + q.trim().toLowerCase() + "%");
            query.setParameter("id", q.trim());
        }
        return query.getResultList();
    }

    public List<Booking> findByCustomer(Session session, Integer customerId) {
        return session.createQuery("from Booking b where b.customer.customerId = :c order by b.bookingId desc", Booking.class)
                .setParameter("c", customerId).getResultList();
    }

    public List<Booking> findRecent(Session session, int max) {
        return session.createQuery("from Booking b order by b.bookingId desc", Booking.class)
                .setMaxResults(max).getResultList();
    }

    /** Bookings that can still receive payments / services (not cancelled). */
    public List<Booking> findNotCancelled(Session session) {
        return session.createQuery("from Booking b where b.status <> 'Cancelled' order by b.bookingId desc", Booking.class)
                .getResultList();
    }

    public long countByStatus(Session session, String status) {
        return session.createQuery("select count(b) from Booking b where b.status = :s", Long.class)
                .setParameter("s", status).getSingleResult();
    }

    /** True when the room already has an active booking overlapping the dates. */
    public boolean hasOverlap(Session session, Integer roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeBookingId) {
        String hql = "select count(b) from Booking b where b.room.roomId = :room "
                + "and b.status in ('Pending','Confirmed','Checked-In') "
                + "and b.checkIn < :checkOut and b.checkOut > :checkIn "
                + (excludeBookingId != null ? "and b.bookingId <> :ex" : "");
        var q = session.createQuery(hql, Long.class)
                .setParameter("room", roomId)
                .setParameter("checkIn", checkIn)
                .setParameter("checkOut", checkOut);
        if (excludeBookingId != null) q.setParameter("ex", excludeBookingId);
        return q.getSingleResult() > 0;
    }
}
