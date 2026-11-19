package com.hotelmanagement.controller;

import com.hotelmanagement.service.EmployeeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin only (enforced by AuthFilter): employee management. */
@WebServlet(urlPatterns = "/admin/employees")
public class EmployeeServlet extends BaseServlet {

    private final EmployeeService employeeService = new EmployeeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = param(req, "q");
        req.setAttribute("q", q);
        req.setAttribute("employees", employeeService.search(q));
        Integer editId = intParam(req, "edit");
        if (editId != null) req.setAttribute("editEmployee", employeeService.get(editId));
        forward(req, resp, "admin/employees");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String act = action(req);
        if ("add".equals(act)) {
            employeeService.add(param(req, "name"), param(req, "role"), param(req, "phone"), decimalParam(req, "salary"));
            flashSuccess(req, "Employee added.");
        } else if ("update".equals(act)) {
            employeeService.update(intParam(req, "employeeId"), param(req, "name"), param(req, "role"),
                    param(req, "phone"), decimalParam(req, "salary"));
            flashSuccess(req, "Employee updated.");
        } else if ("delete".equals(act)) {
            employeeService.delete(intParam(req, "employeeId"));
            flashSuccess(req, "Employee deleted.");
        }
        redirect(req, resp, "/admin/employees");
    }
}
