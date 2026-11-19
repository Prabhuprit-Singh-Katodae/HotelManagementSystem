package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `employee`. */
@Entity
@Table(name = "employee")
public class Employee implements Identifiable {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "salary")
    private Double salary;  // the existing column is DOUBLE

    public Employee() { }

    @Override public Integer getId() { return employeeId; }
    @Override public void setId(Integer id) { this.employeeId = id; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer v) { this.employeeId = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    public Double getSalary() { return salary; }
    public void setSalary(Double v) { this.salary = v; }


}
