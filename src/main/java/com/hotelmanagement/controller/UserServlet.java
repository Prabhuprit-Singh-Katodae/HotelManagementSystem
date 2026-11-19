package com.hotelmanagement.controller;

import com.hotelmanagement.service.CustomerService;
import com.hotelmanagement.service.EmployeeService;
import com.hotelmanagement.service.UserService;
import com.hotelmanagement.util.LoginUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin only (enforced by AuthFilter): user accounts, roles, activate / deactivate, password reset. */
@WebServlet(urlPatterns = "/admin/users")
public class UserServlet extends BaseServlet {

    private final UserService userService = new UserService();
    private final EmployeeService employeeService = new EmployeeService();
    private final CustomerService customerService = new CustomerService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("users", userService.list());
        req.setAttribute("roles", UserService.ROLES);
        req.setAttribute("employees", employeeService.search(null));
        req.setAttribute("customers", customerService.search(null));
        forward(req, resp, "admin/users");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginUser me = currentUser(req);
        String act = action(req);
        if ("create".equals(act)) {
            userService.create(param(req, "username"), req.getParameter("password"), param(req, "role"),
                    intParam(req, "employeeId"), intParam(req, "customerId"));
            flashSuccess(req, "User account created.");
        } else if ("status".equals(act)) {
            userService.setStatus(intParam(req, "userId"), param(req, "status"), me);
            flashSuccess(req, "Account status updated.");
        } else if ("role".equals(act)) {
            userService.setRole(intParam(req, "userId"), param(req, "role"), me);
            flashSuccess(req, "Role updated.");
        } else if ("password".equals(act)) {
            userService.resetPassword(intParam(req, "userId"), req.getParameter("newPassword"));
            flashSuccess(req, "Password reset.");
        }
        redirect(req, resp, "/admin/users");
    }
}
