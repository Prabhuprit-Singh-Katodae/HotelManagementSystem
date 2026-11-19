package com.hotelmanagement.controller;

import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.service.BookingService;
import com.hotelmanagement.service.CustomerService;
import com.hotelmanagement.service.RoomService;
import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/** Staff: create bookings, list them, confirm / check-in / check-out / cancel. */
@WebServlet(urlPatterns = {"/admin/bookings", "/admin/booking-details"})
public class AdminBookingServlet extends BaseServlet {

    private final BookingService bookingService = new BookingService();
    private final CustomerService customerService = new CustomerService();
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginUser user = currentUser(req);

        if (req.getServletPath().equals("/admin/booking-details")) {
            Integer id = intParam(req, "id");
            Booking booking = bookingService.get(id, user);
            req.setAttribute("booking", booking);
            req.setAttribute("bill", bookingService.getBill(id, user));
            req.setAttribute("history", bookingService.history(id));
            req.setAttribute("nextStatuses", BookingService.nextStatuses(booking.getStatus()));
            forward(req, resp, "admin/booking-details");
            return;
        }

        putDefaultDates(req);
        String status = param(req, "status");
        String q = param(req, "q");
        req.setAttribute("status", status);
        req.setAttribute("q", q);
        req.setAttribute("statuses", BookingService.ALL_STATUSES);
        req.setAttribute("bookings", bookingService.search(status, q));

        // "New booking" panel: pick customer + dates, then choose from the available rooms
        LocalDate checkIn = dateParam(req, "checkIn");
        LocalDate checkOut = dateParam(req, "checkOut");
        boolean showNew = "1".equals(param(req, "newBooking")) || !param(req, "checkIn").isEmpty();
        req.setAttribute("showNew", showNew);
        if (showNew) {
            req.setAttribute("customers", customerService.search(null));
            req.setAttribute("roomTypes", roomService.listTypes());
            req.setAttribute("checkIn", checkIn);
            req.setAttribute("checkOut", checkOut);
            req.setAttribute("selectedCustomer", intParam(req, "customerId"));
            Integer typeId = intParam(req, "typeId");
            req.setAttribute("typeId", typeId);
            if (checkIn != null || checkOut != null) {
                try {
                    req.setAttribute("availableRooms", bookingService.findAvailableRooms(checkIn, checkOut, typeId));
                } catch (ServiceException e) {
                    req.setAttribute("error", e.getMessage());
                }
            }
        }
        forward(req, resp, "admin/bookings");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginUser user = currentUser(req);
        String act = action(req);

        if ("create".equals(act)) {
            LocalDate checkIn = dateParam(req, "checkIn");
            LocalDate checkOut = dateParam(req, "checkOut");
            try {
                Integer id = bookingService.createBooking(intParam(req, "customerId"), intParam(req, "roomId"),
                        checkIn, checkOut, user, Booking.CONFIRMED);
                flashSuccess(req, "Booking #" + id + " created and confirmed.");
                redirect(req, resp, "/admin/booking-details?id=" + id);
            } catch (ServiceException e) {
                flashError(req, e.getMessage());
                redirect(req, resp, "/admin/bookings?newBooking=1&customerId=" + param(req, "customerId")
                        + "&checkIn=" + param(req, "checkIn") + "&checkOut=" + param(req, "checkOut"));
            }
            return;
        }

        if ("status".equals(act)) {
            bookingService.changeStatus(intParam(req, "bookingId"), param(req, "newStatus"), user);
            flashSuccess(req, "Booking status changed to " + param(req, "newStatus") + ".");
        }
        redirect(req, resp, safeBack(req, "/admin/bookings"));
    }
}
