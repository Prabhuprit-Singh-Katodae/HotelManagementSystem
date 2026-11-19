package com.hotelmanagement.controller;

import com.hotelmanagement.service.AuthService;
import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * Guest login (/login), staff/admin login (/admin/login), registration and logout.
 * The two login pages share the same accounts table, but each page only accepts its own kind of user.
 */
@WebServlet(urlPatterns = {"/login", "/admin/login", "/register", "/logout"})
public class AuthServlet extends BaseServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (path.equals("/logout")) {
            LoginUser leaving = currentUser(req);
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();
            flashSuccess(req, "You have been logged out.");
            redirect(req, resp, leaving != null && leaving.isHotelStaff() ? "/admin/login" : "/login");
            return;
        }
        LoginUser user = currentUser(req);
        if (user != null) {
            redirect(req, resp, homeFor(user));
            return;
        }
        req.setAttribute("next", param(req, "next"));
        forward(req, resp, path.equals("/register") ? "customer/register" : (path.equals("/admin/login") ? "admin/login" : "customer/login"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (path.equals("/register")) {
            try {
                authService.register(param(req, "name"), param(req, "phone"), param(req, "email"), param(req, "address"),
                        param(req, "username"), req.getParameter("password"), req.getParameter("confirm"));
                flashSuccess(req, "Registration successful. You can log in now.");
                redirect(req, resp, "/login");
            } catch (ServiceException e) {
                req.setAttribute("error", e.getMessage());
                forward(req, resp, "customer/register");
            }
            return;
        }

        // ---- login (guest page or staff page)
        boolean staffPage = path.equals("/admin/login");
        String loginView = staffPage ? "admin/login" : "customer/login";
        try {
            LoginUser user = authService.login(param(req, "username"), req.getParameter("password"));
            if (staffPage && !user.isHotelStaff()) {
                throw new ServiceException("This is the staff login. Guests please use the guest login.");
            }
            if (!staffPage && user.isHotelStaff()) {
                throw new ServiceException("Hotel staff must sign in on the staff login page.");
            }
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();          // new session after login (prevents session fixation)
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", user);
            session.setAttribute("csrfToken", UUID.randomUUID().toString());
            String next = param(req, "next");
            if (next.startsWith("/") && !next.startsWith("//") && !next.startsWith("/login")
                    && !next.contains("\n") && !next.contains("\r")) {
                boolean allowed = (!next.startsWith("/admin/") || user.isHotelStaff())
                        && (!next.startsWith("/customer/") || user.isCustomer());
                if (allowed) {
                    redirect(req, resp, next);
                    return;
                }
            }
            redirect(req, resp, homeFor(user));
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("next", param(req, "next"));
            forward(req, resp, loginView);
        }
    }

    private String homeFor(LoginUser user) {
        return user.isHotelStaff() ? "/admin/dashboard" : "/home";
    }
}
