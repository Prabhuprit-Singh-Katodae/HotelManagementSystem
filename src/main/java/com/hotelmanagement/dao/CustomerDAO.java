package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Customer;
import java.util.List;
import org.hibernate.Session;

public class CustomerDAO extends BaseDAO<Customer> {

    public CustomerDAO() {
        super(Customer.class, "customerId", true);
    }

    /** Search by name, phone or email. An empty search returns everyone. */
    public List<Customer> search(Session session, String q) {
        if (q == null || q.isBlank()) return findAll(session);
        return session.createQuery(
                "from Customer c where lower(c.name) like :q or c.phone like :q or lower(c.email) like :q order by c.customerId",
                Customer.class)
                .setParameter("q", "%" + q.trim().toLowerCase() + "%")
                .getResultList();
    }

    public Customer findByEmail(Session session, String email) {
        return session.createQuery("from Customer c where lower(c.email) = :e", Customer.class)
                .setParameter("e", email.toLowerCase())
                .setMaxResults(1).uniqueResult();
    }

    public Customer findByPhone(Session session, String phone) {
        return session.createQuery("from Customer c where c.phone = :p", Customer.class)
                .setParameter("p", phone)
                .setMaxResults(1).uniqueResult();
    }
}
