package com.hotelmanagement.controller;

import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parent of all servlets: small helpers for reading parameters, flash messages,
 * redirects and forwarding to JSP pages. Any ServiceException (a message meant for the user)
 * thrown inside a servlet is shown as a red alert on the next page.
 */
public abstract class BaseServlet extends HttpServlet {

    protected static final String VIEWS = "/WEB-INF/views/";

    // ---------------------------------------------------------------- error handling
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (ServiceException e) {
            flashError(req, e.getMessage());
            if ("POST".equalsIgnoreCase(req.getMethod())) {
                redirect(req, resp, safeBack(req, req.getServletPath()));
            } else {
                String path = req.getServletPath();
                boolean adminPage = path.startsWith("/admin/") && !path.equals("/admin/dashboard");
                redirect(req, resp, adminPage ? "/admin/dashboard" : "/home");
            }
        }
    }

    // ---------------------------------------------------------------- views / redirects
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view) throws ServletException, IOException {
        req.getRequestDispatcher(VIEWS + view + ".jsp").forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** Uses the "back" form field when it is a safe local path, otherwise the default. */
    protected String safeBack(HttpServletRequest req, String defaultPath) {
        String back = req.getParameter("back");
        if (back != null && back.startsWith("/") && !back.startsWith("//") && !back.contains("\n") && !back.contains("\r")) {
            return back;
        }
        return defaultPath;
    }

    // ---------------------------------------------------------------- session / flash messages
    protected LoginUser currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (LoginUser) session.getAttribute("loggedUser");
    }

    /** The customer id of the logged-in customer (throws if the account is not linked to a customer). */
    protected Integer customerIdOf(HttpServletRequest req) {
        LoginUser u = currentUser(req);
        if (u == null || u.getCustomerId() == null) {
            throw new ServiceException("Your account is not linked to a customer profile. Please contact the hotel.");
        }
        return u.getCustomerId();
    }

    protected void flashSuccess(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashSuccess", message);
    }

    protected void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    // ---------------------------------------------------------------- parameters
    protected String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    protected Integer intParam(HttpServletRequest req, String name) {
        try {
            String v = param(req, name);
            return v.isEmpty() ? null : Integer.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected BigDecimal decimalParam(HttpServletRequest req, String name) {
        try {
            String v = param(req, name);
            return v.isEmpty() ? null : new BigDecimal(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected LocalDate dateParam(HttpServletRequest req, String name) {
        try {
            String v = param(req, name);
            return v.isEmpty() ? null : LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    protected String action(HttpServletRequest req) {
        return param(req, "action");
    }

    /** Puts today's date and tomorrow's date in the request (used as default/min values in date inputs). */
    protected void putDefaultDates(HttpServletRequest req) {
        req.setAttribute("today", LocalDate.now());
        req.setAttribute("tomorrow", LocalDate.now().plusDays(1));
    }
}
