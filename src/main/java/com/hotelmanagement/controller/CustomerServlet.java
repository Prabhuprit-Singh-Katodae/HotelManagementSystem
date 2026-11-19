package com.hotelmanagement.controller;

import com.hotelmanagement.entity.Customer;
import com.hotelmanagement.service.AuthService;
import com.hotelmanagement.service.CustomerService;
import com.hotelmanagement.util.LoginUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Staff: customer management (/admin/customers). Customers: own profile (/customer/profile). */
@WebServlet(urlPatterns = {"/admin/customers", "/customer/profile"})
public class CustomerServlet extends BaseServlet {

    private final CustomerService customerService = new CustomerService();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().equals("/customer/profile")) {
            req.setAttribute("customer", customerService.get(customerIdOf(req)));
            forward(req, resp, "customer/profile");
            return;
        }
        String q = param(req, "q");
        req.setAttribute("q", q);
        req.setAttribute("customers", customerService.search(q));
        Integer editId = intParam(req, "edit");
        if (editId != null) req.setAttribute("editCustomer", customerService.get(editId));
        forward(req, resp, "admin/customers");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String act = action(req);

        // ------------- customer profile
        if (req.getServletPath().equals("/customer/profile")) {
            LoginUser user = currentUser(req);
            if ("password".equals(act)) {
                authService.changePassword(user.getUserId(), req.getParameter("currentPassword"),
                        req.getParameter("newPassword"), req.getParameter("confirmPassword"));
                flashSuccess(req, "Your password was changed.");
            } else {
                Integer id = customerIdOf(req);
                customerService.update(id, param(req, "name"), param(req, "phone"), param(req, "email"), param(req, "address"));
                Customer fresh = customerService.get(id);
                req.getSession().setAttribute("loggedUser", new LoginUser(user.getUserId(), user.getUsername(),
                        user.getRole(), user.getCustomerId(), user.getEmployeeId(), fresh.getName()));
                flashSuccess(req, "Your profile was updated.");
            }
            redirect(req, resp, "/customer/profile");
            return;
        }

        // ------------- staff: customer management
        String back = safeBack(req, "/admin/customers");
        if ("add".equals(act)) {
            customerService.add(param(req, "name"), param(req, "phone"), param(req, "email"), param(req, "address"));
            flashSuccess(req, "Customer added.");
        } else if ("update".equals(act)) {
            customerService.update(intParam(req, "customerId"), param(req, "name"), param(req, "phone"),
                    param(req, "email"), param(req, "address"));
            flashSuccess(req, "Customer updated.");
        } else if ("delete".equals(act)) {
            customerService.delete(intParam(req, "customerId"));
            flashSuccess(req, "Customer deleted.");
        }
        redirect(req, resp, back);
    }
}
