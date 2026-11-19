package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.entity.BookingStatusHistory;
import com.hotelmanagement.entity.UserAccount;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.Session;

public class BookingStatusHistoryDAO extends BaseDAO<BookingStatusHistory> {

    public BookingStatusHistoryDAO() {
        super(BookingStatusHistory.class, "historyId", false); // AUTO_INCREMENT table
    }

    /** Writes one history row. Call this in the SAME transaction as the booking change. */
    public void log(Session session, Booking booking, String oldStatus, String newStatus, UserAccount changedBy) {
        BookingStatusHistory h = new BookingStatusHistory();
        h.setBooking(booking);
        h.setOldStatus(oldStatus);
        h.setNewStatus(newStatus);
        h.setChangedBy(changedBy);
        h.setChangedAt(LocalDateTime.now());
        save(session, h);
    }

    public List<BookingStatusHistory> findByBooking(Session session, Integer bookingId) {
        return session.createQuery("from BookingStatusHistory h where h.booking.bookingId = :b order by h.historyId",
                BookingStatusHistory.class).setParameter("b", bookingId).getResultList();
    }
}
