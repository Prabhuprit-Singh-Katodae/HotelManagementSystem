package com.hotelmanagement.dao;

import com.hotelmanagement.entity.HotelService;
import java.util.List;
import org.hibernate.Session;

/** DAO for the `service` table (entity class HotelService). */
public class ServiceDAO extends BaseDAO<HotelService> {

    public ServiceDAO() {
        super(HotelService.class, "serviceId", true);
    }

    public List<HotelService> findAllByName(Session session) {
        return session.createQuery("from HotelService s order by s.serviceName", HotelService.class).getResultList();
    }
}
