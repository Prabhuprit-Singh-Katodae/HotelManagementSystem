package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `room_type`. */
@Entity
@Table(name = "room_type")
public class RoomType implements Identifiable {

    @Id
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms = new ArrayList<>();

    public RoomType() { }

    @Override public Integer getId() { return typeId; }
    @Override public void setId(Integer id) { this.typeId = id; }

    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer v) { this.typeId = v; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String v) { this.typeName = v; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal v) { this.price = v; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> v) { this.rooms = v; }


    /** Name of the local picture used on room cards (stored in assets/images). */
    public String getImageFile() {
        String n = typeName == null ? "" : typeName.toLowerCase();
        if (n.contains("suite")) return "room-suite.svg";
        if (n.contains("deluxe")) return "room-deluxe.svg";
        if (n.contains("double")) return "room-double.svg";
        if (n.contains("single")) return "room-single.svg";
        return "room-default.svg";
    }

}
