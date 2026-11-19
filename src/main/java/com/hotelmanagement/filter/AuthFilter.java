package com.hotelmanagement.filter;

import com.hotelmanagement.util.LoginUser;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Server-side security for EVERY request:
 *  - /admin/login is public (staff sign-in page)
 *  - /admin/*     only ADMIN or STAFF   (/admin/employees and /admin/users: ADMIN only)
 *  - /customer/*  only CUSTOMER
 *  - every POST must carry the CSRF token stored in the session
 *  - "delete" actions in /admin/* are ADMIN only
 */
@WebFilter(urlPatterns = "/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");

        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");

        String path = req.getServletPath();
        if (path.startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        LoginUser user = session == null ? null : (LoginUser) session.getAttribute("loggedUser");
        boolean isPost = "POST".equalsIgnoreCase(req.getMethod());

        // ---------------- admin area (the staff login page itself is public)
        if (path.startsWith("/admin/") && !path.equals("/admin/login")) {
            if (user == null) {
                redirectToLogin(req, resp, isPost);
                return;
            }
            if (!user.isHotelStaff()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only hotel staff can open this page.");
                return;
            }
            boolean adminOnlyPage = path.equals("/admin/employees") || path.equals("/admin/users");
            boolean deleteAction = isPost && "delete".equals(req.getParameter("action"));
            if ((adminOnlyPage || deleteAction) && !user.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This action is for administrators only.");
                return;
            }
        }

        // ---------------- customer area
        if (path.startsWith("/customer/")) {
            if (user == null) {
                redirectToLogin(req, resp, isPost);
                return;
            }
            if (!user.isCustomer()) {
                if (user.isHotelStaff()) {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                } else {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                return;
            }
        }

        // ---------------- CSRF check for every form submit (login/register have no session token yet)
        if (isPost && !path.equals("/login") && !path.equals("/admin/login") && !path.equals("/register")) {
            String expected = session == null ? null : (String) session.getAttribute("csrfToken");
            String sent = req.getParameter("_csrf");
            if (expected == null || !expected.equals(sent)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Your session expired or the form was invalid. Please go back and try again.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp, boolean isPost) throws IOException {
        String target = req.getContextPath()
                + (req.getServletPath().startsWith("/admin/") ? "/admin/login" : "/login");
        if (!isPost) {
            String next = req.getServletPath() + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
            target += "?next=" + URLEncoder.encode(next, StandardCharsets.UTF_8);
        }
        resp.sendRedirect(target);
    }
}
