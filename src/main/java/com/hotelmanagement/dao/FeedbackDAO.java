package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Feedback;
import java.util.List;
import org.hibernate.Session;

public class FeedbackDAO extends BaseDAO<Feedback> {

    public FeedbackDAO() {
        super(Feedback.class, "feedbackId", true);
    }

    public List<Feedback> findAllNewestFirst(Session session) {
        return session.createQuery("from Feedback f order by f.feedbackId desc", Feedback.class).getResultList();
    }

    public List<Feedback> findByCustomer(Session session, Integer customerId) {
        return session.createQuery("from Feedback f where f.customer.customerId = :c order by f.feedbackId desc", Feedback.class)
                .setParameter("c", customerId).getResultList();
    }

    public List<Feedback> findTop(Session session, int min, int max) {
        return session.createQuery("from Feedback f where f.rating >= :min and f.comments is not null order by f.feedbackId desc", Feedback.class)
                .setParameter("min", min).setMaxResults(max).getResultList();
    }

    public Double average(Session session) {
        return session.createQuery("select avg(f.rating) from Feedback f", Double.class).getSingleResult();
    }
}
