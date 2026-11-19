package com.hotelmanagement.controller;

import com.hotelmanagement.service.FeedbackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Customers submit feedback (/customer/feedback); staff view it (/admin/feedback). */
@WebServlet(urlPatterns = {"/customer/feedback", "/admin/feedback"})
public class FeedbackServlet extends BaseServlet {

    private final FeedbackService feedbackService = new FeedbackService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().equals("/customer/feedback")) {
            req.setAttribute("feedbacks", feedbackService.listForCustomer(customerIdOf(req)));
            forward(req, resp, "customer/my-feedback");
        } else {
            req.setAttribute("feedbacks", feedbackService.listAll());
            req.setAttribute("avgRating", feedbackService.average());
            forward(req, resp, "admin/feedback");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String act = action(req);
        if (path.equals("/customer/feedback") && "submit".equals(act)) {
            feedbackService.submit(customerIdOf(req), intParam(req, "rating"), param(req, "comments"));
            flashSuccess(req, "Thank you! Your feedback was submitted.");
        } else if (path.equals("/admin/feedback") && "delete".equals(act)) {
            feedbackService.delete(intParam(req, "feedbackId"));
            flashSuccess(req, "Feedback deleted.");
        }
        redirect(req, resp, path);
    }
}
