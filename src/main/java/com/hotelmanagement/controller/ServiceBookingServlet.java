package com.hotelmanagement.controller;

import com.hotelmanagement.service.BookingService;
import com.hotelmanagement.service.ServiceBookingService;
import com.hotelmanagement.service.ServiceCatalogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Staff: assign services to bookings and change quantities. */
@WebServlet(urlPatterns = "/admin/service-bookings")
public class ServiceBookingServlet extends BaseServlet {

    private final ServiceBookingService serviceBookingService = new ServiceBookingService();
    private final ServiceCatalogService catalogService = new ServiceCatalogService();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer bookingId = intParam(req, "bookingId");
        putDefaultDates(req);
        req.setAttribute("bookingId", bookingId);
        req.setAttribute("serviceBookings", serviceBookingService.list(bookingId));
        req.setAttribute("services", catalogService.list());
        req.setAttribute("bookings", bookingService.notCancelled());
        forward(req, resp, "admin/service-bookings");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String act = action(req);
        if ("add".equals(act)) {
            serviceBookingService.add(intParam(req, "bookingId"), intParam(req, "serviceId"),
                    intParam(req, "quantity"), dateParam(req, "serviceDate"));
            flashSuccess(req, "Service added to the booking.");
        } else if ("update".equals(act)) {
            serviceBookingService.updateQuantity(intParam(req, "serviceBookingId"), intParam(req, "quantity"));
            flashSuccess(req, "Quantity updated.");
        } else if ("remove".equals(act)) {
            serviceBookingService.remove(intParam(req, "serviceBookingId"));
            flashSuccess(req, "Service removed from the booking.");
        }
        redirect(req, resp, safeBack(req, "/admin/service-bookings"));
    }
}
