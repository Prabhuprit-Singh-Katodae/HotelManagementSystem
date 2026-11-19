package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `user_account`. */
@Entity
@Table(name = "user_account")
public class UserAccount implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "status", length = 20)
    private String status;

    public UserAccount() { }

    @Override public Integer getId() { return userId; }
    @Override public void setId(Integer id) { this.userId = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer v) { this.userId = v; }

    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }

    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer v) { this.customer = v; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee v) { this.employee = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }


    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STAFF = "STAFF";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

}
