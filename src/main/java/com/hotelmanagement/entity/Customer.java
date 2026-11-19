package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `customer`. */
@Entity
@Table(name = "customer")
public class Customer implements Identifiable {

    @Id
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @OneToMany(mappedBy = "customer")
    private List<Booking> bookings = new ArrayList<>();

    public Customer() { }

    @Override public Integer getId() { return customerId; }
    @Override public void setId(Integer id) { this.customerId = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer v) { this.customerId = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> v) { this.bookings = v; }


}
