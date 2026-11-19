package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `booking_status_history`. */
@Entity
@Table(name = "booking_status_history")
public class BookingStatusHistory implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "old_status", length = 30)
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 30)
    private String newStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by")
    private UserAccount changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    public BookingStatusHistory() { }

    @Override public Integer getId() { return historyId; }
    @Override public void setId(Integer id) { this.historyId = id; }

    public Integer getHistoryId() { return historyId; }
    public void setHistoryId(Integer v) { this.historyId = v; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking v) { this.booking = v; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String v) { this.oldStatus = v; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String v) { this.newStatus = v; }

    public UserAccount getChangedBy() { return changedBy; }
    public void setChangedBy(UserAccount v) { this.changedBy = v; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime v) { this.changedAt = v; }


}
