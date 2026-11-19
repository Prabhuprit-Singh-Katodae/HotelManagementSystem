package com.hotelmanagement.controller;

import com.hotelmanagement.service.BookingService;
import com.hotelmanagement.service.RoomService;
import com.hotelmanagement.util.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Search for rooms that are free between two dates. */
@WebServlet(urlPatterns = "/availability")
public class AvailabilityServlet extends BaseServlet {

    private final BookingService bookingService = new BookingService();
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        putDefaultDates(req);
        req.setAttribute("roomTypes", roomService.listTypes());

        LocalDate checkIn = dateParam(req, "checkIn");
        LocalDate checkOut = dateParam(req, "checkOut");
        Integer typeId = intParam(req, "typeId");
        req.setAttribute("checkIn", checkIn);
        req.setAttribute("checkOut", checkOut);
        req.setAttribute("typeId", typeId);

        boolean submitted = !param(req, "checkIn").isEmpty() || !param(req, "checkOut").isEmpty();
        if (submitted) {
            try {
                req.setAttribute("rooms", bookingService.findAvailableRooms(checkIn, checkOut, typeId));
                req.setAttribute("nights", ChronoUnit.DAYS.between(checkIn, checkOut));
                req.setAttribute("searched", true);
            } catch (ServiceException e) {
                req.setAttribute("error", e.getMessage());
            }
        }
        forward(req, resp, "customer/availability");
    }
}
