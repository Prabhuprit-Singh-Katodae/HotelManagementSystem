package com.hotelmanagement.dao;

import com.hotelmanagement.entity.UserAccount;
import java.util.List;
import org.hibernate.Session;

public class UserAccountDAO extends BaseDAO<UserAccount> {

    public UserAccountDAO() {
        super(UserAccount.class, "userId", false); // AUTO_INCREMENT table
    }

    public UserAccount findByUsername(Session session, String username) {
        return session.createQuery("from UserAccount u where lower(u.username) = :n", UserAccount.class)
                .setParameter("n", username.toLowerCase()).setMaxResults(1).uniqueResult();
    }

    public UserAccount findByCustomerId(Session session, Integer customerId) {
        return session.createQuery("from UserAccount u where u.customer.customerId = :c", UserAccount.class)
                .setParameter("c", customerId).setMaxResults(1).uniqueResult();
    }

    public long countByRole(Session session, String role) {
        return session.createQuery("select count(u) from UserAccount u where upper(u.role) = :r", Long.class)
                .setParameter("r", role.toUpperCase()).getSingleResult();
    }

    public long countActiveByRole(Session session, String role) {
        return session.createQuery(
                "select count(u) from UserAccount u where upper(u.role) = :r and (u.status is null or upper(u.status) = 'ACTIVE')",
                Long.class).setParameter("r", role.toUpperCase()).getSingleResult();
    }

    public List<UserAccount> findAllByUsername(Session session) {
        return session.createQuery("from UserAccount u order by u.username", UserAccount.class).getResultList();
    }
}
