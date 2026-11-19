package com.hotelmanagement.service;

import com.hotelmanagement.dao.BookingDAO;
import com.hotelmanagement.dao.ServiceBookingDAO;
import com.hotelmanagement.dao.ServiceDAO;
import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.entity.HotelService;
import com.hotelmanagement.entity.ServiceBooking;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.ServiceException;
import java.time.LocalDate;
import java.util.List;

/** Services (breakfast, laundry...) assigned to a booking. */
public class ServiceBookingService {

    private final ServiceBookingDAO sbDAO = new ServiceBookingDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    public List<ServiceBooking> list(Integer bookingId) {
        return HibernateUtil.read(s -> sbDAO.findFiltered(s, bookingId));
    }

    private static int checkQuantity(Integer q) {
        if (q == null || q < 1 || q > 100) throw new ServiceException("Quantity must be between 1 and 100.");
        return q;
    }

    public void add(Integer bookingId, Integer serviceId, Integer quantity, LocalDate date) {
        int qty = checkQuantity(quantity);
        HibernateUtil.txRun(s -> {
            Booking b = bookingDAO.findById(s, bookingId);
            if (b == null) throw new ServiceException("Please choose a booking.");
            if (Booking.CANCELLED.equalsIgnoreCase(b.getStatus()) || Booking.CHECKED_OUT.equalsIgnoreCase(b.getStatus())) {
                throw new ServiceException("Services cannot be added to a " + b.getStatus() + " booking.");
            }
            HotelService h = serviceDAO.findById(s, serviceId);
            if (h == null) throw new ServiceException("Please choose a service.");
            ServiceBooking sb = new ServiceBooking();
            sb.setBooking(b);
            sb.setService(h);
            sb.setQuantity(qty);
            sb.setServiceDate(date == null ? LocalDate.now() : date);
            sbDAO.save(s, sb);
        });
    }

    public void updateQuantity(Integer id, Integer quantity) {
        int qty = checkQuantity(quantity);
        HibernateUtil.txRun(s -> {
            ServiceBooking sb = sbDAO.findById(s, id);
            if (sb == null) throw new ServiceException("Service booking not found.");
            sb.setQuantity(qty);
        });
    }

    public void remove(Integer id) {
        HibernateUtil.txRun(s -> {
            ServiceBooking sb = sbDAO.findById(s, id);
            if (sb == null) throw new ServiceException("Service booking not found.");
            sbDAO.delete(s, sb);
        });
    }
}
