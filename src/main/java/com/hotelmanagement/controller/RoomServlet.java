package com.hotelmanagement.controller;

import com.hotelmanagement.entity.Room;
import com.hotelmanagement.service.FeedbackService;
import com.hotelmanagement.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Public pages: home page, room list and room details. */
@WebServlet(urlPatterns = {"/home", "/rooms", "/room"})
public class RoomServlet extends BaseServlet {

    private final RoomService roomService = new RoomService();
    private final FeedbackService feedbackService = new FeedbackService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        putDefaultDates(req);

        if (path.equals("/home")) {
            req.setAttribute("roomTypes", roomService.listTypes());
            req.setAttribute("reviews", feedbackService.topReviews(3));
            forward(req, resp, "customer/index");

        } else if (path.equals("/rooms")) {
            Integer typeId = intParam(req, "type");
            req.setAttribute("rooms", roomService.listRooms(typeId));
            req.setAttribute("roomTypes", roomService.listTypes());
            req.setAttribute("selectedType", typeId);
            forward(req, resp, "customer/rooms");

        } else { // /room?id=
            Room room = roomService.getRoom(intParam(req, "id"));
            req.setAttribute("room", room);
            forward(req, resp, "customer/room-details");
        }
    }
}
