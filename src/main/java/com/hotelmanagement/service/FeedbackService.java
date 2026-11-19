package com.hotelmanagement.service;

import com.hotelmanagement.dao.CustomerDAO;
import com.hotelmanagement.dao.FeedbackDAO;
import com.hotelmanagement.entity.Customer;
import com.hotelmanagement.entity.Feedback;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;
import java.time.LocalDate;
import java.util.List;

public class FeedbackService {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public void submit(Integer customerId, Integer rating, String comments) {
        if (rating == null || rating < 1 || rating > 5) throw new ServiceException("Please choose a rating from 1 to 5.");
        String text = Validator.optional(comments, "Comments", 1000);
        HibernateUtil.txRun(s -> {
            Customer c = customerDAO.findById(s, customerId);
            if (c == null) throw new ServiceException("Customer not found.");
            Feedback f = new Feedback();
            f.setCustomer(c);
            f.setRating(rating);
            f.setComments(text);
            f.setFeedbackDate(LocalDate.now());
            feedbackDAO.save(s, f);
        });
    }

    public List<Feedback> listAll() {
        return HibernateUtil.read(s -> feedbackDAO.findAllNewestFirst(s));
    }

    public List<Feedback> listForCustomer(Integer customerId) {
        return HibernateUtil.read(s -> feedbackDAO.findByCustomer(s, customerId));
    }

    /** Good reviews shown on the home page. */
    public List<Feedback> topReviews(int max) {
        return HibernateUtil.read(s -> feedbackDAO.findTop(s, 4, max));
    }

    public double average() {
        Double d = HibernateUtil.read(s -> feedbackDAO.average(s));
        return d == null ? 0 : Math.round(d * 10.0) / 10.0;
    }

    public void delete(Integer id) {
        HibernateUtil.txRun(s -> {
            Feedback f = feedbackDAO.findById(s, id);
            if (f == null) throw new ServiceException("Feedback not found.");
            feedbackDAO.delete(s, f);
        });
    }
}
