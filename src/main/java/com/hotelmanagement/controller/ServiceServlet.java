package com.hotelmanagement.controller;

import com.hotelmanagement.service.ServiceCatalogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Staff: the list of hotel services (breakfast, laundry...). */
@WebServlet(urlPatterns = "/admin/services")
public class ServiceServlet extends BaseServlet {

    private final ServiceCatalogService catalogService = new ServiceCatalogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("services", catalogService.list());
        Integer editId = intParam(req, "edit");
        if (editId != null) req.setAttribute("editService", catalogService.get(editId));
        forward(req, resp, "admin/services");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String act = action(req);
        if ("add".equals(act)) {
            catalogService.add(param(req, "serviceName"), decimalParam(req, "price"));
            flashSuccess(req, "Service added.");
        } else if ("update".equals(act)) {
            catalogService.update(intParam(req, "serviceId"), param(req, "serviceName"), decimalParam(req, "price"));
            flashSuccess(req, "Service updated.");
        } else if ("delete".equals(act)) {
            catalogService.delete(intParam(req, "serviceId"));
            flashSuccess(req, "Service deleted.");
        }
        redirect(req, resp, "/admin/services");
    }
}
