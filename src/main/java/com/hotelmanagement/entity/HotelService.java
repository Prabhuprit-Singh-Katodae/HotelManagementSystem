package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `service`. */
@Entity
@Table(name = "service")
public class HotelService implements Identifiable {

    @Id
    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    public HotelService() { }

    @Override public Integer getId() { return serviceId; }
    @Override public void setId(Integer id) { this.serviceId = id; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer v) { this.serviceId = v; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String v) { this.serviceName = v; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal v) { this.price = v; }


}
