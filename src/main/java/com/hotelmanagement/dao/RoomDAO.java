package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Room;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class RoomDAO extends BaseDAO<Room> {

    public RoomDAO() {
        super(Room.class, "roomId", true);
    }

    public List<Room> findByType(Session session, Integer typeId) {
        if (typeId == null) {
            return session.createQuery("from Room r order by r.roomNumber", Room.class).getResultList();
        }
        return session.createQuery("from Room r where r.roomType.typeId = :t order by r.roomNumber", Room.class)
                .setParameter("t", typeId).getResultList();
    }

    public Room findByRoomNumber(Session session, String roomNumber) {
        return session.createQuery("from Room r where r.roomNumber = :n", Room.class)
                .setParameter("n", roomNumber).setMaxResults(1).uniqueResult();
    }

    public long countByStatus(Session session, String status) {
        return session.createQuery("select count(r) from Room r where lower(r.status) = :s", Long.class)
                .setParameter("s", status.toLowerCase()).getSingleResult();
    }

    /**
     * Rooms that are NOT in maintenance and have NO active booking overlapping [checkIn, checkOut).
     * Two stays overlap when existing.checkIn < new.checkOut AND existing.checkOut > new.checkIn.
     * The Room.status column is deliberately NOT used for future availability.
     */
    public List<Room> findAvailable(Session session, LocalDate checkIn, LocalDate checkOut, Integer typeId) {
        StringBuilder hql = new StringBuilder("from Room r where lower(r.status) <> 'maintenance' ");
        if (typeId != null) hql.append("and r.roomType.typeId = :typeId ");
        hql.append("and not exists (select 1 from Booking b where b.room = r ")
           .append("and b.status in ('Pending','Confirmed','Checked-In') ")
           .append("and b.checkIn < :checkOut and b.checkOut > :checkIn) ")
           .append("order by r.roomType.price, r.roomNumber");
        Query<Room> q = session.createQuery(hql.toString(), Room.class);
        q.setParameter("checkIn", checkIn);
        q.setParameter("checkOut", checkOut);
        if (typeId != null) q.setParameter("typeId", typeId);
        return q.getResultList();
    }
}
