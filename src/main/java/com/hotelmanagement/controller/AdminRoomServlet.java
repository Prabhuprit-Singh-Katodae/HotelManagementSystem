package com.hotelmanagement.controller;

import com.hotelmanagement.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Staff: manage rooms (/admin/rooms) and room types (/admin/room-types). */
@WebServlet(urlPatterns = {"/admin/rooms", "/admin/room-types"})
public class AdminRoomServlet extends BaseServlet {

    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer editId = intParam(req, "edit");
        if (req.getServletPath().equals("/admin/room-types")) {
            req.setAttribute("roomTypes", roomService.listTypes());
            if (editId != null) req.setAttribute("editType", roomService.getType(editId));
            forward(req, resp, "admin/room-types");
        } else {
            Integer typeFilter = intParam(req, "type");
            req.setAttribute("rooms", roomService.listRooms(typeFilter));
            req.setAttribute("roomTypes", roomService.listTypes());
            req.setAttribute("roomStatuses", RoomService.STATUSES);
            req.setAttribute("selectedType", typeFilter);
            if (editId != null) req.setAttribute("editRoom", roomService.getRoom(editId));
            forward(req, resp, "admin/rooms");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String act = action(req);
        String path = req.getServletPath();

        if (path.equals("/admin/room-types")) {
            if ("add".equals(act)) {
                roomService.addType(param(req, "typeName"), decimalParam(req, "price"));
                flashSuccess(req, "Room type added.");
            } else if ("update".equals(act)) {
                roomService.updateType(intParam(req, "typeId"), param(req, "typeName"), decimalParam(req, "price"));
                flashSuccess(req, "Room type updated.");
            } else if ("delete".equals(act)) {
                roomService.deleteType(intParam(req, "typeId"));
                flashSuccess(req, "Room type deleted.");
            }
        } else {
            if ("add".equals(act)) {
                roomService.addRoom(param(req, "roomNumber"), intParam(req, "typeId"), param(req, "status"));
                flashSuccess(req, "Room added.");
            } else if ("update".equals(act)) {
                roomService.updateRoom(intParam(req, "roomId"), param(req, "roomNumber"), intParam(req, "typeId"), param(req, "status"));
                flashSuccess(req, "Room updated.");
            } else if ("status".equals(act)) {
                roomService.setStatus(intParam(req, "roomId"), param(req, "status"));
                flashSuccess(req, "Room status updated.");
            } else if ("delete".equals(act)) {
                roomService.deleteRoom(intParam(req, "roomId"));
                flashSuccess(req, "Room deleted.");
            }
        }
        redirect(req, resp, path);
    }
}
