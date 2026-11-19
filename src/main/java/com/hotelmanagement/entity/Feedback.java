package com.hotelmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity for table `feedback`. */
@Entity
@Table(name = "feedback")
public class Feedback implements Identifiable {

    @Id
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "feedback_date")
    private LocalDate feedbackDate;

    public Feedback() { }

    @Override public Integer getId() { return feedbackId; }
    @Override public void setId(Integer id) { this.feedbackId = id; }

    public Integer getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Integer v) { this.feedbackId = v; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer v) { this.customer = v; }

    public Integer getRating() { return rating; }
    public void setRating(Integer v) { this.rating = v; }

    public String getComments() { return comments; }
    public void setComments(String v) { this.comments = v; }

    public LocalDate getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(LocalDate v) { this.feedbackDate = v; }


}
