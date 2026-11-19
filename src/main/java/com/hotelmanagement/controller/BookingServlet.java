package com.hotelmanagement.controller;

import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.entity.Room;
import com.hotelmanagement.service.BookingService;
import com.hotelmanagement.service.RoomService;
import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Customer side of bookings: book a room, see own bookings, cancel. */
@WebServlet(urlPatterns = {"/customer/book", "/customer/bookings", "/customer/booking-confirmation"})
public class BookingServlet extends BaseServlet {

    private final BookingService bookingService = new BookingService();
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        LoginUser user = currentUser(req);

        if (path.equals("/customer/book")) {
            Integer roomId = intParam(req, "roomId");
            LocalDate checkIn = dateParam(req, "checkIn");
            LocalDate checkOut = dateParam(req, "checkOut");
            if (roomId == null || checkIn == null || checkOut == null) {
                flashError(req, "Please choose your dates first.");
                redirect(req, resp, roomId == null ? "/availability" : "/room?id=" + roomId);
                return;
            }
            Room room = roomService.getRoom(roomId);
            BookingService.validateDates(checkIn, checkOut);
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            BigDecimal rate = room.getRoomType() == null ? BigDecimal.ZERO : room.getRoomType().getPrice();
            req.setAttribute("room", room);
            req.setAttribute("checkIn", checkIn);
            req.setAttribute("checkOut", checkOut);
            req.setAttribute("nights", nights);
            req.setAttribute("total", rate.multiply(BigDecimal.valueOf(nights)));
            req.setAttribute("available", bookingService.isRoomAvailable(roomId, checkIn, checkOut));
            forward(req, resp, "customer/booking-form");

        } else if (path.equals("/customer/booking-confirmation")) {
            Integer id = intParam(req, "id");
            req.setAttribute("booking", bookingService.get(id, user));
            req.setAttribute("bill", bookingService.getBill(id, user));
            forward(req, resp, "customer/booking-confirmation");

        } else { // /customer/bookings
            req.setAttribute("bookings", bookingService.forCustomer(customerIdOf(req)));
            forward(req, resp, "customer/my-bookings");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        LoginUser user = currentUser(req);

        if (path.equals("/customer/book")) {
            LocalDate checkIn = dateParam(req, "checkIn");
            LocalDate checkOut = dateParam(req, "checkOut");
            try {
                Integer id = bookingService.createBooking(customerIdOf(req), intParam(req, "roomId"),
                        checkIn, checkOut, user, Booking.PENDING);
                redirect(req, resp, "/customer/booking-confirmation?id=" + id);
            } catch (ServiceException e) {
                flashError(req, e.getMessage());
                redirect(req, resp, "/availability" + (checkIn != null && checkOut != null
                        ? "?checkIn=" + checkIn + "&checkOut=" + checkOut : ""));
            }

        } else if (path.equals("/customer/bookings") && "cancel".equals(action(req))) {
            bookingService.changeStatus(intParam(req, "bookingId"), Booking.CANCELLED, user);
            flashSuccess(req, "Your booking was cancelled.");
            redirect(req, resp, "/customer/bookings");

        } else {
            redirect(req, resp, "/customer/bookings");
        }
    }
}
