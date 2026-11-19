package com.hotelmanagement.service;

import com.hotelmanagement.dao.BookingDAO;
import com.hotelmanagement.dao.BookingStatusHistoryDAO;
import com.hotelmanagement.dao.CustomerDAO;
import com.hotelmanagement.dao.PaymentDAO;
import com.hotelmanagement.dao.RoomDAO;
import com.hotelmanagement.dao.ServiceBookingDAO;
import com.hotelmanagement.dao.UserAccountDAO;
import com.hotelmanagement.entity.Booking;
import com.hotelmanagement.entity.BookingStatusHistory;
import com.hotelmanagement.entity.Customer;
import com.hotelmanagement.entity.Payment;
import com.hotelmanagement.entity.Room;
import com.hotelmanagement.entity.ServiceBooking;
import com.hotelmanagement.entity.UserAccount;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.ServiceException;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;

/** The most important service: availability, booking creation, status changes and bills. */
public class BookingService {

    public static final List<String> ALL_STATUSES = List.of(
            Booking.PENDING, Booking.CONFIRMED, Booking.CHECKED_IN, Booking.CHECKED_OUT, Booking.CANCELLED);
    private static final int MAX_NIGHTS = 60;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final UserAccountDAO userDAO = new UserAccountDAO();
    private final BookingStatusHistoryDAO historyDAO = new BookingStatusHistoryDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();

    // ------------------------------------------------------------ rules

    /** Check-in must not be in the past, check-out must be AFTER check-in. */
    public static void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) throw new ServiceException("Please choose both check-in and check-out dates.");
        if (checkIn.isBefore(LocalDate.now())) throw new ServiceException("Check-in date cannot be in the past.");
        if (!checkOut.isAfter(checkIn)) throw new ServiceException("Check-out date must be after the check-in date.");
        if (ChronoUnit.DAYS.between(checkIn, checkOut) > MAX_NIGHTS) {
            throw new ServiceException("You can book at most " + MAX_NIGHTS + " nights at a time.");
        }
    }

    /** Allowed next statuses for a booking status (the status flow). */
    public static List<String> nextStatuses(String status) {
        String st = status == null ? Booking.PENDING : status.trim();
        if (Booking.PENDING.equalsIgnoreCase(st)) return List.of(Booking.CONFIRMED, Booking.CANCELLED);
        if (Booking.CONFIRMED.equalsIgnoreCase(st)) return List.of(Booking.CHECKED_IN, Booking.CANCELLED);
        if (Booking.CHECKED_IN.equalsIgnoreCase(st)) return List.of(Booking.CHECKED_OUT);
        return List.of();
    }

    private static String normalizeStatus(String status) {
        for (String s : ALL_STATUSES) {
            if (s.equalsIgnoreCase(status == null ? "" : status.trim())) return s;
        }
        throw new ServiceException("Unknown booking status.");
    }

    // ------------------------------------------------------------ availability

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer typeId) {
        validateDates(checkIn, checkOut);
        return HibernateUtil.read(s -> roomDAO.findAvailable(s, checkIn, checkOut, typeId));
    }

    public boolean isRoomAvailable(Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return HibernateUtil.read(s -> {
            Room r = roomDAO.findById(s, roomId);
            if (r == null) throw new ServiceException("Room not found.");
            if (Room.MAINTENANCE.equalsIgnoreCase(r.getStatus())) return false;
            return !bookingDAO.hasOverlap(s, roomId, checkIn, checkOut, null);
        });
    }

    // ------------------------------------------------------------ create

    /**
     * Creates a booking inside ONE transaction. The room row is locked first and the
     * overlap check is repeated, so two people cannot book the same room for the same dates.
     */
    public Integer createBooking(Integer customerId, Integer roomId, LocalDate checkIn, LocalDate checkOut,
                                 LoginUser actor, String initialStatus) {
        validateDates(checkIn, checkOut);
        final String status = normalizeStatus(initialStatus);
        return HibernateUtil.tx(s -> {
            Room room = s.find(Room.class, roomId, LockModeType.PESSIMISTIC_WRITE);
            if (room == null) throw new ServiceException("Room not found.");
            if (Room.MAINTENANCE.equalsIgnoreCase(room.getStatus())) {
                throw new ServiceException("Room " + room.getRoomNumber() + " is under maintenance.");
            }
            Customer customer = customerDAO.findById(s, customerId);
            if (customer == null) throw new ServiceException("Please choose a customer.");
            if (bookingDAO.hasOverlap(s, roomId, checkIn, checkOut, null)) {
                throw new ServiceException("Sorry, room " + room.getRoomNumber()
                        + " was just booked for these dates. Please choose another room.");
            }
            Booking b = new Booking();
            b.setCustomer(customer);
            b.setRoom(room);
            b.setCheckIn(checkIn);
            b.setCheckOut(checkOut);
            b.setStatus(status);
            bookingDAO.save(s, b);
            historyDAO.log(s, b, null, status, actorEntity(s, actor));
            return b.getBookingId();
        });
    }

    private UserAccount actorEntity(Session s, LoginUser actor) {
        return actor == null ? null : userDAO.findById(s, actor.getUserId());
    }

    // ------------------------------------------------------------ status changes

    /** Moves a booking to a new status (checks the allowed flow, updates the room, writes history). */
    public void changeStatus(Integer bookingId, String newStatus, LoginUser actor) {
        final String target = normalizeStatus(newStatus);
        HibernateUtil.txRun(s -> {
            Booking b = bookingDAO.findById(s, bookingId);
            if (b == null) throw new ServiceException("Booking not found.");

            if (actor.isCustomer()) {
                if (b.getCustomer() == null || !b.getCustomer().getCustomerId().equals(actor.getCustomerId())) {
                    throw new ServiceException("Booking not found.");
                }
                if (!Booking.CANCELLED.equals(target)) throw new ServiceException("Customers can only cancel bookings.");
            }

            String old = b.getStatus();
            if (!nextStatuses(old).contains(target)) {
                throw new ServiceException("A booking that is " + old + " cannot be changed to " + target + ".");
            }

            Room room = s.find(Room.class, b.getRoom().getRoomId(), LockModeType.PESSIMISTIC_WRITE);
            if (Booking.CHECKED_IN.equals(target)) {
                if (Room.OCCUPIED.equalsIgnoreCase(room.getStatus())) {
                    throw new ServiceException("Room " + room.getRoomNumber() + " is still occupied by another guest.");
                }
                if (Room.MAINTENANCE.equalsIgnoreCase(room.getStatus())) {
                    throw new ServiceException("Room " + room.getRoomNumber() + " is under maintenance.");
                }
                room.setStatus(Room.OCCUPIED);
            } else if (Booking.CHECKED_OUT.equals(target)) {
                room.setStatus(Room.AVAILABLE);
            }

            b.setStatus(target);
            historyDAO.log(s, b, old, target, actorEntity(s, actor));
        });
    }

    // ------------------------------------------------------------ reading

    public List<Booking> search(String status, String q) {
        return HibernateUtil.read(s -> bookingDAO.search(s, status, q));
    }

    public List<Booking> forCustomer(Integer customerId) {
        return HibernateUtil.read(s -> bookingDAO.findByCustomer(s, customerId));
    }

    public List<Booking> notCancelled() {
        return HibernateUtil.read(s -> bookingDAO.findNotCancelled(s));
    }

    /** Loads a booking. When the actor is a customer, it must be THEIR booking. */
    public Booking get(Integer bookingId, LoginUser actor) {
        Booking b = HibernateUtil.read(s -> bookingDAO.findById(s, bookingId));
        checkAccess(b, actor);
        return b;
    }

    private void checkAccess(Booking b, LoginUser actor) {
        if (b == null) throw new ServiceException("Booking not found.");
        if (actor != null && actor.isCustomer()
                && (b.getCustomer() == null || !b.getCustomer().getCustomerId().equals(actor.getCustomerId()))) {
            throw new ServiceException("Booking not found.");
        }
    }

    public List<BookingStatusHistory> history(Integer bookingId) {
        return HibernateUtil.read(s -> historyDAO.findByBooking(s, bookingId));
    }

    // ------------------------------------------------------------ billing

    public BillSummary getBill(Integer bookingId, LoginUser actor) {
        return HibernateUtil.read(s -> {
            Booking b = bookingDAO.findById(s, bookingId);
            checkAccess(b, actor);
            return buildBill(s, b);
        });
    }

    /** Bills with an outstanding balance for one customer (used on "My Payments"). */
    public List<BillSummary> dueBillsForCustomer(Integer customerId) {
        return HibernateUtil.read(s -> {
            List<BillSummary> list = new ArrayList<>();
            for (Booking b : bookingDAO.findByCustomer(s, customerId)) {
                if (Booking.CANCELLED.equalsIgnoreCase(b.getStatus())) continue;
                BillSummary bill = buildBill(s, b);
                if (bill.getBalance().signum() > 0) list.add(bill);
            }
            return list;
        });
    }

    /** Bills with an outstanding balance for all bookings (used on the staff payment form). */
    public List<BillSummary> dueBills() {
        return HibernateUtil.read(s -> {
            List<BillSummary> list = new ArrayList<>();
            for (Booking b : bookingDAO.findNotCancelled(s)) {
                BillSummary bill = buildBill(s, b);
                if (bill.getBalance().signum() > 0) list.add(bill);
            }
            return list;
        });
    }

    /** Calculates room charges + service charges - payments for a booking (inside an open session). */
    public BillSummary buildBill(Session s, Booking b) {
        BillSummary bill = new BillSummary();
        bill.setBooking(b);
        long nights = b.getNights();
        BigDecimal rate = BigDecimal.ZERO;
        if (b.getRoom() != null && b.getRoom().getRoomType() != null && b.getRoom().getRoomType().getPrice() != null) {
            rate = b.getRoom().getRoomType().getPrice();
        }
        BigDecimal roomCharges = rate.multiply(BigDecimal.valueOf(nights));

        List<ServiceBooking> lines = serviceBookingDAO.findByBooking(s, b.getBookingId());
        BigDecimal serviceCharges = BigDecimal.ZERO;
        for (ServiceBooking sb : lines) serviceCharges = serviceCharges.add(sb.getLineTotal());

        List<Payment> payments = paymentDAO.findByBooking(s, b.getBookingId());
        BigDecimal paid = BigDecimal.ZERO;
        Set<String> methods = new LinkedHashSet<>();
        for (Payment p : payments) {
            if (p.getAmount() != null) paid = paid.add(p.getAmount());
            if (p.getPaymentMethod() != null) methods.add(p.getPaymentMethod());
        }

        BigDecimal total = roomCharges.add(serviceCharges);
        BigDecimal balance = total.subtract(paid);
        if (balance.signum() < 0) balance = BigDecimal.ZERO;

        bill.setNights(nights);
        bill.setRoomRate(rate);
        bill.setRoomCharges(roomCharges);
        bill.setServiceCharges(serviceCharges);
        bill.setTotal(total);
        bill.setPaid(paid);
        bill.setBalance(balance);
        bill.setPayments(payments);
        bill.setServiceLines(lines);
        bill.setPaymentMethods(methods.isEmpty() ? "-" : String.join(", ", methods));
        if (total.signum() > 0 && paid.compareTo(total) >= 0) bill.setPaymentStatus("Paid");
        else if (paid.signum() > 0) bill.setPaymentStatus("Partially Paid");
        else bill.setPaymentStatus("Unpaid");
        return bill;
    }
}
