package com.hotelmanagement.service;

import com.hotelmanagement.dao.BookingDAO;
import com.hotelmanagement.dao.CustomerDAO;
import com.hotelmanagement.dao.PaymentDAO;
import com.hotelmanagement.dao.RoomDAO;
import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.entity.Room;
import com.hotelmanagement.util.HibernateUtil;
import java.util.LinkedHashMap;
import java.util.Map;

/** Numbers for the admin dashboard. Everything is counted from the database. */
public class DashboardService {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public Map<String, Object> loadStats() {
        return HibernateUtil.read(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("totalCustomers", customerDAO.count(s));
            m.put("totalRooms", roomDAO.count(s));
            m.put("availableRooms", roomDAO.countByStatus(s, Room.AVAILABLE));
            m.put("occupiedRooms", roomDAO.countByStatus(s, Room.OCCUPIED));
            m.put("maintenanceRooms", roomDAO.countByStatus(s, Room.MAINTENANCE));
            m.put("totalBookings", bookingDAO.count(s));
            m.put("pendingBookings", bookingDAO.countByStatus(s, Booking.PENDING));
            m.put("revenue", paymentDAO.sumAll(s));

            // One entry per booking status (used for the simple bar chart)
            Map<String, Long> byStatus = new LinkedHashMap<>();
            for (String st : BookingService.ALL_STATUSES) byStatus.put(st, bookingDAO.countByStatus(s, st));
            m.put("bookingsByStatus", byStatus);
            m.put("recentBookings", bookingDAO.findRecent(s, 6));
            return m;
        });
    }
}
