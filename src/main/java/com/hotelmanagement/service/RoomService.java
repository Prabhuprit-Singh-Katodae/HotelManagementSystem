package com.hotelmanagement.service;

import com.hotelmanagement.dao.RoomDAO;
import com.hotelmanagement.dao.RoomTypeDAO;
import com.hotelmanagement.entity.Room;
import com.hotelmanagement.entity.RoomType;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;
import java.math.BigDecimal;
import java.util.List;

/** Rooms and room types. */
public class RoomService {

    public static final List<String> STATUSES = List.of(Room.AVAILABLE, Room.OCCUPIED, Room.MAINTENANCE);

    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomTypeDAO typeDAO = new RoomTypeDAO();

    // ---------------- rooms
    public List<Room> listRooms(Integer typeId) {
        return HibernateUtil.read(s -> roomDAO.findByType(s, typeId));
    }

    public Room getRoom(Integer id) {
        Room r = HibernateUtil.read(s -> roomDAO.findById(s, id));
        if (r == null) throw new ServiceException("Room not found.");
        return r;
    }

    private static String normalizeStatus(String status) {
        for (String s : STATUSES) {
            if (s.equalsIgnoreCase(status == null ? "" : status.trim())) return s;
        }
        throw new ServiceException("Invalid room status.");
    }

    public void addRoom(String roomNumber, Integer typeId, String status) {
        String number = Validator.required(roomNumber, "Room number", 20);
        String st = normalizeStatus(status);
        HibernateUtil.txRun(s -> {
            RoomType type = typeDAO.findById(s, typeId);
            if (type == null) throw new ServiceException("Please choose a room type.");
            if (roomDAO.findByRoomNumber(s, number) != null) throw new ServiceException("Room " + number + " already exists.");
            Room r = new Room();
            r.setRoomNumber(number);
            r.setRoomType(type);
            r.setStatus(st);
            roomDAO.save(s, r);
        });
    }

    public void updateRoom(Integer id, String roomNumber, Integer typeId, String status) {
        String number = Validator.required(roomNumber, "Room number", 20);
        String st = normalizeStatus(status);
        HibernateUtil.txRun(s -> {
            Room r = roomDAO.findById(s, id);
            if (r == null) throw new ServiceException("Room not found.");
            RoomType type = typeDAO.findById(s, typeId);
            if (type == null) throw new ServiceException("Please choose a room type.");
            Room same = roomDAO.findByRoomNumber(s, number);
            if (same != null && !same.getRoomId().equals(id)) throw new ServiceException("Room " + number + " already exists.");
            r.setRoomNumber(number);
            r.setRoomType(type);
            r.setStatus(st);
        });
    }

    public void setStatus(Integer id, String status) {
        String st = normalizeStatus(status);
        HibernateUtil.txRun(s -> {
            Room r = roomDAO.findById(s, id);
            if (r == null) throw new ServiceException("Room not found.");
            r.setStatus(st);
        });
    }

    public void deleteRoom(Integer id) {
        HibernateUtil.txRun(s -> {
            Room r = roomDAO.findById(s, id);
            if (r == null) throw new ServiceException("Room not found.");
            roomDAO.delete(s, r);
        });
    }

    // ---------------- room types
    public List<RoomType> listTypes() {
        return HibernateUtil.read(s -> typeDAO.findAllByPrice(s));
    }

    public RoomType getType(Integer id) {
        RoomType t = HibernateUtil.read(s -> typeDAO.findById(s, id));
        if (t == null) throw new ServiceException("Room type not found.");
        return t;
    }

    public void addType(String name, BigDecimal price) {
        String n = Validator.required(name, "Type name", 50);
        BigDecimal p = Validator.money(price, "Price", false);
        HibernateUtil.txRun(s -> {
            if (typeDAO.findByName(s, n) != null) throw new ServiceException("This room type already exists.");
            RoomType t = new RoomType();
            t.setTypeName(n);
            t.setPrice(p);
            typeDAO.save(s, t);
        });
    }

    public void updateType(Integer id, String name, BigDecimal price) {
        String n = Validator.required(name, "Type name", 50);
        BigDecimal p = Validator.money(price, "Price", false);
        HibernateUtil.txRun(s -> {
            RoomType t = typeDAO.findById(s, id);
            if (t == null) throw new ServiceException("Room type not found.");
            RoomType same = typeDAO.findByName(s, n);
            if (same != null && !same.getTypeId().equals(id)) throw new ServiceException("This room type already exists.");
            t.setTypeName(n);
            t.setPrice(p);
        });
    }

    public void deleteType(Integer id) {
        HibernateUtil.txRun(s -> {
            RoomType t = typeDAO.findById(s, id);
            if (t == null) throw new ServiceException("Room type not found.");
            typeDAO.delete(s, t);
        });
    }
}
