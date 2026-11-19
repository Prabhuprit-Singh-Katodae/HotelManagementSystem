package com.hotelmanagement.dao;

import com.hotelmanagement.entity.ServiceBooking;
import java.util.List;
import org.hibernate.Session;

public class ServiceBookingDAO extends BaseDAO<ServiceBooking> {

    public ServiceBookingDAO() {
        super(ServiceBooking.class, "serviceBookingId", true);
    }

    public List<ServiceBooking> findFiltered(Session session, Integer bookingId) {
        if (bookingId == null) {
            return session.createQuery("from ServiceBooking sb order by sb.serviceBookingId desc", ServiceBooking.class)
                    .getResultList();
        }
        return findByBooking(session, bookingId);
    }

    public List<ServiceBooking> findByBooking(Session session, Integer bookingId) {
        return session.createQuery("from ServiceBooking sb where sb.booking.bookingId = :b order by sb.serviceBookingId",
                ServiceBooking.class).setParameter("b", bookingId).getResultList();
    }
}
