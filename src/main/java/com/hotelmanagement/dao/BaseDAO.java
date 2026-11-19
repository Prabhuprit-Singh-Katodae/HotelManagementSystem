package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Identifiable;
import java.util.List;
import org.hibernate.Session;

/**
 * Generic DAO with the common operations. Every method receives the Hibernate Session
 * from the Service layer, so the Service decides where a transaction starts and ends.
 *
 * ID strategy: the first 9 tables may or may not be AUTO_INCREMENT, so (manualId = true)
 * the DAO assigns MAX(id)+1 itself. This works in both cases. The two new tables
 * (user_account, booking_status_history) use AUTO_INCREMENT (manualId = false).
 */
public abstract class BaseDAO<T extends Identifiable> {

    private final Class<T> entityClass;
    private final String idField;
    private final boolean manualId;

    protected BaseDAO(Class<T> entityClass, String idField, boolean manualId) {
        this.entityClass = entityClass;
        this.idField = idField;
        this.manualId = manualId;
    }

    protected String entityName() {
        return entityClass.getSimpleName();
    }

    public T findById(Session session, Integer id) {
        return id == null ? null : session.get(entityClass, id);
    }

    public List<T> findAll(Session session) {
        return session.createQuery("from " + entityName() + " e order by e." + idField, entityClass).getResultList();
    }

    public long count(Session session) {
        return session.createQuery("select count(e) from " + entityName() + " e", Long.class).getSingleResult();
    }

    /** Inserts a new row (assigns an id first when manualId is true). */
    public T save(Session session, T entity) {
        if (manualId && entity.getId() == null) {
            Integer max = session.createQuery(
                    "select coalesce(max(e." + idField + "), 0) from " + entityName() + " e", Integer.class)
                    .getSingleResult();
            entity.setId(max + 1);
        }
        session.persist(entity);
        return entity;
    }

    /** Deletes a row. The entity must have been loaded in this same session. */
    public void delete(Session session, T entity) {
        session.remove(entity);
    }
}
