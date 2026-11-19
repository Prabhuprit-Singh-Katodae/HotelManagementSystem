package com.hotelmanagement.dao;

import com.hotelmanagement.entity.RoomType;
import java.util.List;
import org.hibernate.Session;

public class RoomTypeDAO extends BaseDAO<RoomType> {

    public RoomTypeDAO() {
        super(RoomType.class, "typeId", true);
    }

    public List<RoomType> findAllByPrice(Session session) {
        return session.createQuery("from RoomType t order by t.price, t.typeId", RoomType.class).getResultList();
    }

    public RoomType findByName(Session session, String name) {
        return session.createQuery("from RoomType t where lower(t.typeName) = :n", RoomType.class)
                .setParameter("n", name.toLowerCase()).setMaxResults(1).uniqueResult();
    }
}
