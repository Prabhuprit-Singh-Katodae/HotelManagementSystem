package com.hotelmanagement.controller;

import com.hotelmanagement.service.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin dashboard: statistics counted from the database. */
@WebServlet(urlPatterns = "/admin/dashboard")
public class DashboardServlet extends BaseServlet {

    private final DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("stats", dashboardService.loadStats());
        forward(req, resp, "admin/dashboard");
    }
}
