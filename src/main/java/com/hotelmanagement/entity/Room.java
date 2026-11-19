package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `room`. */
@Entity
@Table(name = "room")
public class Room implements Identifiable {

    @Id
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    private RoomType roomType;

    @Column(name = "status", length = 30)
    private String status;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings = new ArrayList<>();

    public Room() { }

    @Override public Integer getId() { return roomId; }
    @Override public void setId(Integer id) { this.roomId = id; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer v) { this.roomId = v; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String v) { this.roomNumber = v; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType v) { this.roomType = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> v) { this.bookings = v; }


    public static final String AVAILABLE = "Available";
    public static final String OCCUPIED = "Occupied";
    public static final String MAINTENANCE = "Maintenance";

}
