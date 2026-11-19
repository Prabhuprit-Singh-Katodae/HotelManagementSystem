package com.hotelmanagement.controller;

import com.hotelmanagement.service.BookingService;
import com.hotelmanagement.service.PaymentService;
import com.hotelmanagement.util.LoginUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Payments and invoices for staff (/admin/...) and for customers (/customer/...). */
@WebServlet(urlPatterns = {"/admin/payments", "/admin/invoice", "/customer/payments", "/customer/invoice"})
public class PaymentServlet extends BaseServlet {

    private final PaymentService paymentService = new PaymentService();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        LoginUser user = currentUser(req);
        req.setAttribute("methods", PaymentService.METHODS);
        putDefaultDates(req);

        if (path.endsWith("/invoice")) {
            req.setAttribute("bill", bookingService.getBill(intParam(req, "bookingId"), user));
            req.setAttribute("staffView", user.isHotelStaff());
            forward(req, resp, "admin/invoice");   // one invoice page, shared by staff and customers

        } else if (path.equals("/customer/payments")) {
            Integer customerId = customerIdOf(req);
            req.setAttribute("payments", paymentService.listForCustomer(customerId));
            req.setAttribute("dueBills", bookingService.dueBillsForCustomer(customerId));
            forward(req, resp, "customer/my-payments");

        } else { // /admin/payments
            req.setAttribute("payments", paymentService.listAll());
            req.setAttribute("dueBills", bookingService.dueBills());
            req.setAttribute("selectedBooking", intParam(req, "bookingId"));
            forward(req, resp, "admin/payments");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String act = action(req);
        LoginUser user = currentUser(req);

        if (path.equals("/customer/payments") && "pay".equals(act)) {
            paymentService.demoPayFullBalance(intParam(req, "bookingId"), param(req, "method"), user);
            flashSuccess(req, "Demo payment recorded. (No real money was charged.)");
        } else if (path.equals("/admin/payments")) {
            if ("record".equals(act)) {
                paymentService.record(intParam(req, "bookingId"), decimalParam(req, "amount"),
                        param(req, "method"), dateParam(req, "paymentDate"), user);
                flashSuccess(req, "Payment recorded.");
            } else if ("delete".equals(act)) {
                paymentService.delete(intParam(req, "paymentId"));
                flashSuccess(req, "Payment deleted.");
            }
        }
        redirect(req, resp, path);
    }
}
